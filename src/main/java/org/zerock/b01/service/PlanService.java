package org.zerock.b01.service;

import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.domain.TransportChild;
import org.zerock.b01.domain.TransportParent;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface PlanService {

    PageResponseDTO<PlanSetDTO> list(PageRequestDTO pageRequestDTO);

    PlanSetDTO InitReadOne(Long planNo);

    PlanSetDTO LastReadOne(String username);

    Long registerInit(PlanSetDTO planSetDTO);

    GetXYResponse getXY(GetXYRequest getXYRequest);

    Map<String, Object> startTime(Long planNo, String Address, Double mapx, Double mapy, String writer);

    List<PlanPlaceDTO> listOfPlanPlaceAll(Long planNo);

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
                .title(planSet.getTitle())
                .planNo(planSet.getPlanNo())
                .writer(planSet.getWriter())
                .isCar(planSet.getIsCar())
                .ps_startDate(planSet.getPs_startDate())
                .build();

        return planSetDTO;
    }
}
