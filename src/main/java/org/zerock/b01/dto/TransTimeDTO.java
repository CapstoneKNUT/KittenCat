package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransTimeDTO {

    private String writer;

    private LocalDateTime t_startdatetime;

    private String start_location;

    private String arrive_location;

    private Boolean isCar;
}
