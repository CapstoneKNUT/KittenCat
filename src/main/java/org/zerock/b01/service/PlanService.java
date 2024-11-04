package org.zerock.b01.service;

import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.domain.TransportChild;
import org.zerock.b01.domain.TransportParent;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.Search.DrivingRequest;
import org.zerock.b01.dto.Search.DrivingResponse;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface PlanService {

    PlanSetDTO InitReadOne(Long planNo);

    Long registerInit(PlanSetDTO planSetDTO);

    GetXYResponse getXY(GetXYRequest getXYRequest);

    Map<String, Object> startTime(Long planNo, String Address, Double mapx, Double mapy, String writer);

    List<PlanPlaceDTO> listOfPlanPlace(Long planNo, Integer day);

    List<TransportParentDTO> listOfTransportParent(Long planNo, Long ppOrd, Integer day);

    List<TransportChildDTO> listOfTransportChild(Long tno);

    void removePlanSet(Long planNo);

    void removePlanPlace(Long planNo, Long ppOrd);

    void updatePlanPlaceTime(Long planNo, Long ppOrd, LocalTime takeTime);

    default PlanSet dtoToEntity(PlanSetDTO planSetDTO) {

        PlanSet planSet = PlanSet.builder()
                .title(planSetDTO.getTitle())
                .planNo(planSetDTO.getPlanNo())
                .writer(planSetDTO.getWriter())
                .isCar(planSetDTO.getIsCar())
                .ps_startDate(planSetDTO.getPs_startDate())

                .build();

        return planSet;
    }

    default PlanSetDTO entityToDTO(PlanSet planSet) {

        PlanSetDTO planSetDTO = PlanSetDTO.builder()
                .planNo(planSet.getPlanNo())
                .writer(planSet.getWriter())
                .isCar(planSet.getIsCar())
                .ps_startDate(planSet.getPs_startDate())
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
