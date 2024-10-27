package org.zerock.b01.service;

import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.domain.TransportChild;
import org.zerock.b01.domain.TransportParent;
import org.zerock.b01.dto.PlanPlaceDTO;
import org.zerock.b01.dto.PlanSetDTO;
import org.zerock.b01.dto.Search.DrivingRequest;
import org.zerock.b01.dto.Search.DrivingResponse;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;
import org.zerock.b01.dto.TransportChildDTO;
import org.zerock.b01.dto.TransportParentDTO;

import java.time.LocalDateTime;
import java.util.Map;

public interface PlanService {

    PlanSetDTO InitReadOne(Long planNo);

    Long registerInit(PlanSetDTO planSetDTO);

    GetXYResponse getXY(GetXYRequest getXYRequest);

    DrivingResponse getTime(DrivingRequest drivingRequest);

    Map<String,Object> startTime(Long planNo, String Address, Float mapx, Float mapy, String writer);

    PlanPlaceDTO readPlanPlace(Long ppOrd);

    TransportParentDTO readTransportParent(Long tno);

    TransportChildDTO readTransportChild(Long tord);

    void removePlanSet(Long planNo);

    void removePlanPlace(Long ppOrd);

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

    default TransportParentDTO entityToDTOTP(TransportParent transportParent) {

        TransportParentDTO transportParentDTO = TransportParentDTO.builder()
                .tno(transportParent.getTno())
                .ppOrd(transportParent.getPlanPlace().getPpOrd())
                .isCar(transportParent.getIsCar())
                .t_method(transportParent.getT_method())
                .t_startDateTime(transportParent.getT_startDateTime())
                .t_takeTime(transportParent.getT_takeTime())
                .t_goalDateTime(transportParent.getT_goalDateTime())
                .writer(transportParent.getWriter())
                .build();

        return transportParentDTO;
    }

    default TransportChildDTO entityToDTOTP(TransportChild transportChild) {

        TransportChildDTO transportChildDTO = TransportChildDTO.builder()
                .tord(transportChild.getTord())
                .tno(transportChild.getTransportParent().getTno())
                .c_method(transportChild.getC_method())
                .c_takeTime(transportChild.getC_takeTime())
                .build();

        return transportChildDTO;
    }
}
