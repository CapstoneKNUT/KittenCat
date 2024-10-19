package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.dto.PlanPlaceDTO;
import org.zerock.b01.dto.PlanSetDTO;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;
import org.zerock.b01.dto.StoreDTO;
import org.zerock.b01.naver.dto.SearchLocalReq;
import org.zerock.b01.naver.dto.SearchLocalRes;
import org.zerock.b01.repository.PlanPlaceRepository;
import org.zerock.b01.service.PlanService;
import org.zerock.b01.service.StoreService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/plan")
@Log4j2
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final StoreService storeService;
    private final PlanPlaceRepository planPlaceRepository;

    @GetMapping("/register/init")
    public ResponseEntity<Void> registerInitGET() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/register")
    public ResponseEntity<Void> registerGET() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/register/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> registerInitPost(@RequestBody PlanSetDTO planSetDTO) {

        Long planNo = planService.registerInit(planSetDTO);

        return ResponseEntity.ok(Map.of("planNo", planNo));
    }

    @PostMapping(value = "/register/{planNo}/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> registerAdd(@RequestBody Long sno, LocalTime takeDate,
            @PathVariable Long planNo) {

        StoreDTO storeDTO = storeService.readOne(sno);

        PlanSetDTO planSetDTO = planService.InitReadOne(planNo);

        String Address = storeDTO.getP_address();

        var search = new GetXYRequest();

        search.setQuery(Address);

        var result = planService.getXY(search);

        GetXYResponse.Address firstAddress = result.getAddresses().get(0);

        float mapx = Float.parseFloat(firstAddress.getX());
        float mapy = Float.parseFloat(firstAddress.getY());

        log.info("mapx: {}, mapy: {}", mapx, mapy);

        PlanPlace planPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);

        PlanPlaceDTO planplaceDTO = PlanPlaceDTO.builder()
                .pp_startAddress(planPlace.getPp_startAddress())
                .pp_takeDate(planPlace.getPp_takeDate())
                .pp_mapx(planPlace.getPp_mapx())
                .pp_mapy(planPlace.getPp_mapy())
                .planNo(planPlace.getPlanSet())
                .build();

        PlanPlace planplace;

        if (planPlace == null) {
            planplace = PlanPlace.builder()
                    .pp_startAddress(Address) // planReposi
                    .pp_startDate(planSetDTO.getStartDate()) // planReposi
                    .pp_takeDate(takeDate) // storeReposi
                    .pp_mapx(mapx) // storeReposi
                    .pp_mapy(mapy) // storeReposi
                    .planSet(new PlanSet(planNo)) // planReposi
                    .build();
        } else {
            LocalDateTime startTime = planService.startTime(planNo, Address, mapx, mapy);

            planplace = PlanPlace.builder()
                    .pp_startAddress(Address)
                    .pp_startDate(startTime)
                    .pp_takeDate(takeDate)
                    .pp_mapx(mapx)
                    .pp_mapy(mapy)
                    .planSet(new PlanSet(planNo))
                    .build();
        }
        Long ppOrd = planPlaceRepository.save(planplace).getPpOrd();

        return ResponseEntity.ok(Map.of("ppOrd", ppOrd));
    }

    @GetMapping(value = "value = /register/{planNo}/trans", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> registerTransGet(@PathVariable Long planNo) {
        PlanPlace planPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);

        return null;
    }
}
