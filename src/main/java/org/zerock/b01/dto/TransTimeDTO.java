package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransTimeDTO {

    private String writer;

    private Integer t_startHour;

    private Integer t_startMinute;

    private String start_location;

    private String arrive_location;

    private Boolean isCar;
}
