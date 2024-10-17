package org.zerock.b01.service;

import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.dto.PlanPlaceDTO;
import org.zerock.b01.dto.PlanSetDTO;
import org.zerock.b01.dto.Search.getXYRequest;

import java.util.Map;

public interface PlanService {

    Long registerInit(PlanSetDTO planSetDTO);

    Map<String, Integer> getXY(getXYRequest getXYRequest);

    Long registerPP(PlanPlaceDTO planPlaceDTO);

    default PlanSet dtoToEntity(PlanSetDTO planSetDTO) {

        PlanSet planSet = PlanSet.builder()
                .planNo(planSetDTO.getPlanNo())
                .writer(planSetDTO.getWriter())
                .isCar(planSetDTO.getIsCar())
                .startDate(planSetDTO.getStartDate())

                .build();

        return planSet;
    }

    default PlanPlace dtoToEntityPP(PlanPlaceDTO planplaceDTO) {

        PlanPlace planplace = PlanPlace.builder()
                .ppOrd(planplaceDTO.getPpOrd())
                .pp_startAddress(planplaceDTO.getPp_startAddress())
                .pp_takeDate(planplaceDTO.getPp_takeDate())
                .pp_mapx(planplaceDTO.getPp_mapx())
                .pp_mapy(planplaceDTO.getPp_mapy())
                .planNo(planplaceDTO.getPlanNo())
                .build();

        return planplace;
    }

}
