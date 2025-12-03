package cerberus.HealthCare.sleep.dto;

import cerberus.HealthCare.sleep.entity.SleepLog;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SleepLog24HResponse {
    private Long sleepId;
    String start;
    String end;

    public static SleepLog24HResponse fromEntity(SleepLog log) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new SleepLog24HResponse(
            log.getId(),
            log.getStart().format(formatter),
            log.getEndTime().format(formatter)
        );
    }
}
