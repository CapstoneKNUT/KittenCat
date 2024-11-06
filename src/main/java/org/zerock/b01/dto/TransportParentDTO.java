package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.b01.domain.PlanSet;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportParentDTO {

    private Long tno;

    private Long ppOrd;

    private Boolean isCar;

    private String t_method;

    private LocalDateTime t_startDateTime;

    private LocalTime t_takeTime;

    private LocalDateTime t_goalDateTime;

    private String writer;

    private Boolean NightToNight;
}
