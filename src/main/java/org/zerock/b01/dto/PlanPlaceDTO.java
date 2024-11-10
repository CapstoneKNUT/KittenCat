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
public class PlanPlaceDTO {

    private Long ppOrd;

    private String pp_title;

    private String pp_startAddress;

    private LocalDateTime pp_startDate;

    private LocalTime pp_takeDate;

    private Double pp_mapx;

    private Double pp_mapy;

    private Byte pp_NightToNight;

    private Long planNo;

}
