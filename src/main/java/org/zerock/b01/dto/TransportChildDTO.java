package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportChildDTO {

    private Long tord;

    private Long tno;

    private String c_method;

    private LocalTime c_takeTime;
}
