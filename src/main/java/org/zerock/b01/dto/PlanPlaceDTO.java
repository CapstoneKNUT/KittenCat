package org.zerock.b01.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.b01.domain.PlanSet;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
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

    private PlanSet planNo;

}
