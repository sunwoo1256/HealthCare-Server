package cerberus.HealthCare.meal.service;

import cerberus.HealthCare.meal.repository.MealRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;

    public long getTodayMealCount(Long userId) {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();        // 00:00
        LocalDateTime end = start.plusDays(1);             // 내일 00:00

        return mealRepository.countByUserIdAndEatTimeBetween(userId, start, end);
    }
}

