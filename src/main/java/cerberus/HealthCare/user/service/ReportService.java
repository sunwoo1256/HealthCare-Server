package cerberus.HealthCare.user.service;

import cerberus.HealthCare.meal.entity.Meal;
import cerberus.HealthCare.meal.repository.MealRepository;
import cerberus.HealthCare.openAI.ChatGPT;
import cerberus.HealthCare.sleep.entity.SleepLog;
import cerberus.HealthCare.sleep.repository.SleepRepository;
import cerberus.HealthCare.user.dto.report.HealthAnalysisResponse;
import cerberus.HealthCare.user.dto.report.MealDto;
import cerberus.HealthCare.user.dto.report.SleepLogDto;
import cerberus.HealthCare.user.entity.HealthReport;
import cerberus.HealthCare.user.entity.User;
import cerberus.HealthCare.user.repository.HealthReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final ChatGPT chatGPT;
    private final HealthReportRepository healthReportRepository;
    private final MealRepository mealRepository;
    private final SleepRepository sleepRepository;


    public String buildPrompt(List<SleepLogDto> sleeps, List<MealDto> meals) {

        return """
                당신은 건강 데이터 분석 전문가입니다.
                아래 제공되는 "수면 정보"와 "식사 정보"를 기반으로 사용자의 건강 상태를 분석하세요.

                분석 규칙:
                1. 위험성이 증가한 질환 2개 + 각 질환의 원인 2개.
                2. 위험성이 감소한 질환 2개 + 각 질환의 원인 2개.
                3. 부족 영양소 1~2개 + 이를 채우기 좋은 음식 3가지.
                4. "반드시 JSON만 출력하라. 코드블록(```json 또는 ```)을 절대 포함하지 마라."
            

                JSON 형식:
                {
                  "increasedDiseases": [
                    { "name": "", "causes": ["", ""] },
                    { "name": "", "causes": ["", ""] }
                  ],
                  "decreasedDiseases": [
                    { "name": "", "causes": ["", ""] },
                    { "name": "", "causes": ["", ""] }
                  ],
                  "nutrientDeficiency": {
                    "nutrients": ["", ""],
                    "recommendedFoods": ["", "", ""]
                  }
                }
                """
            + "\n\n[수면 정보]\n" + sleeps.toString()
            + "\n\n[식사 정보]\n" + meals.toString();
    }

    @Async
    public void updateReportAsync(User user, LocalDateTime end){
        log.info("[ASYNC START] {}", Thread.currentThread().getName());
        LocalDate date = end.toLocalDate();
        String report = generateDailyReport(user, date);

        HealthReport healthReport = healthReportRepository.findByUserAndDate(user, date)
            .orElse(new HealthReport(date, user));
        healthReport.setContent(report);
        healthReportRepository.save(healthReport);
    }


    public String generateDailyReport(User user, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Meal> meals = mealRepository.findByUserIdAndEatTimeBetween(user.getId(), start, end);
        List<SleepLog> sleeps = sleepRepository.findByUserIdAndStartBetween(user.getId(), start, end);

        List<MealDto> mealDtos = meals.stream().map(MealDto::toMealDto).toList();
        List<SleepLogDto> sleepDtos = sleeps.stream().map(SleepLogDto::toSleepLogDto).toList();

        String prompt = buildPrompt(sleepDtos, mealDtos);

        // GPT 호출
        return chatGPT.getCompletionMessageBlocking(prompt);
    }

    public String buildAdvicePrompt(List<SleepLogDto> sleeps, List<MealDto> meals, Integer type) {

        return """
                당신은 건강 데이터 분석 전문가입니다.
                아래 제공되는 "수면 정보"와 "식사 정보"를 기반으로 사용자의 건강 상태를 분석하고 한국어 20자 이내의 구체적인 건강 조언을 하세요.

                분석 규칙:
                0. 1:힐링형/ 2:유머형/ 3:코치형 세가지 형식 중 아래 제공되는 조언 형식의 말투로 각 말투의 특징이 잘 나타나게 조언하라.([조언 형식] "1"이면 힐링형 말투로 조언, [조언 형식] "2"이면 유머형 말투로 조언, [조언 형식] "3"이면 코치형 말투로 조언)
                1. "각각의 형식마다 반드시 한국어 20자 이내 String만 출력하라. 큰따옴표, 작은따옴표, 슬래시 등은 절대 포함하지 마라."
                2. 예시: 오늘은 일찍 주무시는거 어때요?😊, 어서 식사를 하시는게 좋아요!😢 등등
                
                
                """
            + "\n\n[수면 정보]\n" + sleeps.toString()
            + "\n\n[식사 정보]\n" + meals.toString()
            + "\n\n[조언 형식]\n" + type.toString();
    }

    public String generateAdvice(User user, LocalDate date, Integer type) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Meal> meals = mealRepository.findByUserIdAndEatTimeBetween(user.getId(), start, end);
        List<SleepLog> sleeps = sleepRepository.findByUserIdAndStartBetween(user.getId(), start, end);

        List<MealDto> mealDtos = meals.stream().map(MealDto::toMealDto).toList();
        List<SleepLogDto> sleepDtos = sleeps.stream().map(SleepLogDto::toSleepLogDto).toList();

        String prompt = buildAdvicePrompt(sleepDtos, mealDtos, type);

        // GPT 호출
        return chatGPT.getCompletionMessageBlocking(prompt);
    }

}
