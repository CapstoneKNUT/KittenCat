package org.zerock.b01.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.domain.TransportParent;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;
import org.zerock.b01.repository.PlanPlaceRepository;
import org.zerock.b01.repository.TransportParentRepository;
import org.zerock.b01.service.PlanService;
import org.zerock.b01.service.StoreService;
import org.zerock.b01.service.StoreToPlanService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/plan")
@Log4j2
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final StoreService storeService;
    private final StoreToPlanService storeToPlanService;
    private final PlanPlaceRepository planPlaceRepository;
    private final TransportParentRepository transportParentRepository;

    //게시물 등록 초기화면
    @GetMapping("/register/init")
    public ResponseEntity<Void> registerInitGET() {
        return ResponseEntity.ok().build();
    }

    //게시물 등록 화민
    @GetMapping("/register")
    public ResponseEntity<Void> registerGET() {
        return ResponseEntity.ok().build();
    }

    //게시물 초기상태 등록
    @PostMapping(value = "/register/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> registerInitPost(@RequestBody PlanSetDTO planSetDTO) {

        Long planNo = planService.registerInit(planSetDTO);

        return ResponseEntity.ok(Map.of("planNo", planNo));
    }

    //찜목록에서 가져와 일정표에 넣기
    @PostMapping(value = "/register/{planNo}/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Long>>> registerPlanPlaceAdd(@RequestBody PlanPlaceBodyDTO planPlaceBodyDTO,
                                                                  @PathVariable Long planNo) {
        List<Long> ppOrdList = new ArrayList<>();

        StoreDTO storeDTO = storeService.read(planPlaceBodyDTO.getSno());

        storeToPlanService.register(planPlaceBodyDTO.getSno());

        PlanSetDTO planSetDTO = planService.InitReadOne(planNo);

        String Address = storeDTO.getP_address();

        var search = new GetXYRequest();

        search.setQuery(Address);

        var result = planService.getXY(search);

        GetXYResponse.Address firstAddress = result.getAddresses().get(0);

        Float mapx = Float.parseFloat(firstAddress.getX());
        Float mapy = Float.parseFloat(firstAddress.getY());

        log.info("mapx: {}, mapy: {}", mapx, mapy);

        PlanPlace planPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);

        PlanPlace planplace;

        if (planPlace == null) {
            //출발일 == (출발일시 + 머무는 시간)
            if(planSetDTO.getStartDate().toLocalDate() == planSetDTO.getStartDate().plusHours(planPlaceBodyDTO.getTakeDate().getHour()).plusMinutes(planPlaceBodyDTO.getTakeDate().getMinute()).toLocalDate()) {
                planplace = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address) // planReposi
                        .pp_startDate(planSetDTO.getStartDate()) // planReposi
                        .pp_takeDate(planPlaceBodyDTO.getTakeDate()) // storeReposi
                        .pp_mapx(mapx) // storeReposi
                        .pp_mapy(mapy) // storeReposi
                        .planSet(new PlanSet(planNo)) // planReposi
                        .NightToNight(false)
                        .build();
                log.info("planplace 초기화 확인 : {}", planplace);

                ppOrdList.add(planPlaceRepository.save(planplace).getPpOrd());

                return ResponseEntity.ok(Map.of("ppOrd", ppOrdList));
            }else {
                LocalTime endTime = LocalTime.of(23,59,59);

                //23:59:59 - 출발시간
                LocalTime prevTime = endTime.minusHours(planSetDTO.getStartDate().getHour()).minusMinutes(planSetDTO.getStartDate().getMinute()).minusSeconds(planSetDTO.getStartDate().getSecond());
                //머무는 시간 - prevTime + 1초
                LocalTime nextTime = planPlaceBodyDTO.getTakeDate().minusHours(prevTime.getHour()).minusMinutes(prevTime.getMinute()).minusSeconds(prevTime.getSecond()).plusSeconds(1);

                PlanPlace planplace1 = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address) // planReposi
                        .pp_startDate(planSetDTO.getStartDate()) // planReposi
                        .pp_takeDate(prevTime) // storeReposi
                        .pp_mapx(mapx) // storeReposi
                        .pp_mapy(mapy) // storeReposi
                        .planSet(new PlanSet(planNo)) // planReposi
                        .NightToNight(true)
                        .build();
                ppOrdList.add(planPlaceRepository.save(planplace1).getPpOrd());

                PlanPlace planplace2 = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address) // planReposi
                        .pp_startDate(planSetDTO.getStartDate().plusDays(1).withHour(0).withMinute(0).withSecond(0)) // planReposi
                        .pp_takeDate(nextTime) // storeReposi
                        .pp_mapx(mapx) // storeReposi
                        .pp_mapy(mapy) // storeReposi
                        .planSet(new PlanSet(planNo)) // planReposi
                        .NightToNight(true)
                        .build();
                ppOrdList.add(planPlaceRepository.save(planplace2).getPpOrd());
                return ResponseEntity.ok(Map.of("ppOrd", ppOrdList));
            }
        } else {
            Map<String, Object> timeResult = planService.startTime(planNo, Address, mapx, mapy, planSetDTO.getWriter());
            // Map에서 꺼낼 때 필요한 형으로 캐스팅
            LocalDateTime startTime = (LocalDateTime) timeResult.get("pp_startDate");
            Integer getTNumber = (Integer) timeResult.get("getTNumber");

            if(startTime.toLocalDate() == startTime.plusHours(planPlaceBodyDTO.getTakeDate().getHour()).plusMinutes(planPlaceBodyDTO.getTakeDate().getMinute()).plusSeconds(planPlaceBodyDTO.getTakeDate().getSecond()).toLocalDate()){
                planplace = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address)
                        .pp_startDate(startTime)
                        .pp_takeDate(planPlaceBodyDTO.getTakeDate())
                        .pp_mapx(mapx)
                        .pp_mapy(mapy)
                        .planSet(new PlanSet(planNo))
                        .NightToNight(false)
                        .build();

                log.info("planplace 초기화 확인 : {}", planplace);

                ppOrdList.add(planPlaceRepository.save(planplace).getPpOrd());

                return ResponseEntity.ok(Map.of("ppOrd", ppOrdList));
            } else {
                LocalTime endTime = LocalTime.of(23, 59, 59);
                //23:59:59 - 출발시간
                LocalTime prevTime = endTime.minusHours(startTime.getHour()).minusMinutes(startTime.getMinute()).minusSeconds(startTime.getSecond());
                //머무는 시간 - prevTime + 1초
                LocalTime nextTime = planPlaceBodyDTO.getTakeDate().minusHours(prevTime.getHour()).minusMinutes(prevTime.getMinute()).minusSeconds(prevTime.getSecond()).plusSeconds(1);
                PlanPlace planplace1 = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address)
                        .pp_startDate(planSetDTO.getStartDate())
                        .pp_takeDate(prevTime)
                        .pp_mapx(mapx)
                        .pp_mapy(mapy)
                        .planSet(new PlanSet(planNo))
                        .NightToNight(true)
                        .build();
                ppOrdList.add(planPlaceRepository.save(planplace1).getPpOrd());

                PlanPlace planplace2 = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address) // planReposi
                        .pp_startDate(startTime.plusDays(1).withHour(0).withMinute(0).withSecond(0)) // planReposi
                        .pp_takeDate(nextTime) // storeReposi
                        .pp_mapx(mapx) // storeReposi
                        .pp_mapy(mapy) // storeReposi
                        .planSet(new PlanSet(planNo)) // planReposi
                        .NightToNight(true)
                        .build();
                ppOrdList.add(planPlaceRepository.save(planplace2).getPpOrd());
            }
            if(getTNumber == 1) {

                //최신 교통수단 저장내용 조회
                TransportParent LastTransportParent = transportParentRepository.findLastTransportParent(planSetDTO.getWriter());
                //마지막 저장 장소 조회
                PlanPlace LastPlanPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);
                //교통수단의 장소 외래키 수정
                LastTransportParent.setPlanPlace(LastPlanPlace);
                //교통수단 저장
                transportParentRepository.save(LastTransportParent);
            }else if (getTNumber == 2){
                //최신 교통수단 저장내용 조회
                List<TransportParent> LastTransportParent = transportParentRepository.findLastTwoTransportParents(planSetDTO.getWriter());
                TransportParent LastTransportParent1 = LastTransportParent.get(0); // 가장 최신
                TransportParent LastTransportParent2 = LastTransportParent.get(1); // 두 번째 최신

                //마지막 저장 장소 조회
                PlanPlace LastPlanPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);
                //교통수단의 장소 외래키 수정
                LastTransportParent1.setPlanPlace(LastPlanPlace);
                //교통수단 저장
                transportParentRepository.save(LastTransportParent1);
                //교통수단의 장소 외래키 수정
                LastTransportParent2.setPlanPlace(LastPlanPlace);
                //교통수단 저장
                transportParentRepository.save(LastTransportParent2);
            }
            return ResponseEntity.ok(Map.of("ppOrd", ppOrdList));
        }
    }

    //일정표에서 등록된 장소 조회
    @ApiOperation(value = "Read PlanPlace", notes = "Get 방식으로 등록 장소 조회")
    @GetMapping("/planplace/{ppOrd}")
    public ResponseEntity<PlanPlaceDTO> getPlanPlace(@PathVariable Long ppOrd) {
        PlanPlaceDTO planPlaceDTO = planService.readPlanPlace(ppOrd);
        return ResponseEntity.ok(planPlaceDTO);
    }

    //일정표에서 등록된 교통수단 조회
    @ApiOperation(value = "Read TransportParent", notes = "Get 방식으로 등록 장소 조회")
    @GetMapping("/TransportParent/{ppOrd}")
    public ResponseEntity<TransportParentDTO> getTransportParent(@PathVariable Long ppOrd) {
        TransportParentDTO transportParentDTO = planService.readTransportParent(ppOrd);
        return ResponseEntity.ok(transportParentDTO);
    }

    @ApiOperation(value = "Delete PlanSet", notes = "DELETE 방식으로 일정표 삭제")
    @DeleteMapping(value="/{planNo}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> removePlanSet(@PathVariable Long planNo) {
        try {
            planService.removePlanSet(planNo);

            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("planNo", planNo);

            return ResponseEntity.ok(resultMap);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping(value="/planplace/{ppOrd}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> removePlanPlace(@PathVariable Long ppOrd) {
        try {
            planService.removePlanPlace(ppOrd);

            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("ppOrd", ppOrd);

            return ResponseEntity.ok(resultMap);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping(value = "/register/{planNo}/trans", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> registerTransGet(@PathVariable Long planNo) {
        PlanPlace planPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);
        return null;
    }
}
