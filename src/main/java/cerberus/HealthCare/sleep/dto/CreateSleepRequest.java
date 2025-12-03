package cerberus.HealthCare.sleep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSleepRequest {
    private String start;
    private String end;
}
