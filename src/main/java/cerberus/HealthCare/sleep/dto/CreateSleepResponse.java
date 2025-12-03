package cerberus.HealthCare.sleep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSleepResponse {
    private Long userId;
    private Long sleepId;
    private String start;
    private String end;

}
