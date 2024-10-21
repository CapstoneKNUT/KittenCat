package org.zerock.b01.service;

import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.dto.PlanPlaceDTO;
import org.zerock.b01.dto.PlanSetDTO;
import org.zerock.b01.dto.Search.DrivingRequest;
import org.zerock.b01.dto.Search.DrivingResponse;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;

import java.time.LocalDateTime;
import java.util.Map;

public interface PlanService {

    PlanSetDTO InitReadOne(Long planNo);

    PlanPlaceDTO readOne(Long ppOrd);

    Long registerInit(PlanSetDTO planSetDTO);

    GetXYResponse getXY(GetXYRequest getXYRequest);

    Long registerPP(PlanPlaceDTO planPlaceDTO);

    DrivingResponse getTime(DrivingRequest drivingRequest);

    LocalDateTime startTime(Long planNo, String Address, float mapx, float mapy);

    default PlanSet dtoToEntity(PlanSetDTO planSetDTO) {

        PlanSet planSet = PlanSet.builder()
                .planNo(planSetDTO.getPlanNo())
                .writer(planSetDTO.getWriter())
                .isCar(planSetDTO.getIsCar())
                .startDate(planSetDTO.getStartDate())

                .build();

        return planSet;
    }

    default PlanSetDTO entityToDTO(PlanSet planSet) {

        PlanSetDTO planSetDTO = PlanSetDTO.builder()
                .planNo(planSet.getPlanNo())
                .writer(planSet.getWriter())
                .isCar(planSet.getIsCar())
                .startDate(planSet.getStartDate())
                .build();

        return planSetDTO;
    }

    default PlanPlace dtoToEntityPP(PlanPlaceDTO planplaceDTO) {

        PlanPlace planplace = PlanPlace.builder()
                .ppOrd(planplaceDTO.getPpOrd())
                .pp_startAddress(planplaceDTO.getPp_startAddress())
                .pp_takeDate(planplaceDTO.getPp_takeDate())
                .pp_mapx(planplaceDTO.getPp_mapx())
                .pp_mapy(planplaceDTO.getPp_mapy())
                .planSet(planplaceDTO.getPlanNo())
                .build();

        return planplace;
    }

    default PlanPlaceDTO entityToDTOPP(PlanPlace planplace) {

        PlanPlaceDTO planplaceDTO = PlanPlaceDTO.builder()
                .ppOrd(planplace.getPpOrd())
                .pp_startAddress(planplace.getPp_startAddress())
                .pp_takeDate(planplace.getPp_takeDate())
                .pp_mapx(planplace.getPp_mapx())
                .pp_mapy(planplace.getPp_mapy())
                .planNo(planplace.getPlanSet())
                .build();

        return planplaceDTO;
    }

}
