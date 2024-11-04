package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanSetDTO {
    @Size(min = 3, max = 50)
    private String title;

    private Long planNo;

    private Boolean isCar;

    private String writer;

    private LocalDateTime ps_startDate;

}
