package org.zerock.b01.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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

    // 게시물 등록 초기화면
    @GetMapping("/register/init")
    public ResponseEntity<Void> registerInitGET() {
        return ResponseEntity.ok().build();
    }

    // 게시물 초기상태 등록
    @PostMapping(value = "/register/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> registerInitPost(@RequestBody PlanSetDTO planSetDTO) {

        Long planNo = planService.registerInit(planSetDTO);

        return ResponseEntity.ok(Map.of("planNo", planNo));
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<PlanSetDTO>> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<PlanSetDTO> responseDTO = planService.list(pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // 일정표에서 등록된 장소 조회
    @ApiOperation(value = "Read PlanSet", notes = "Get 방식으로 등록 일정 조회")
    @GetMapping("/write")
    public ResponseEntity<PlanSetDTO> DoRegisterGET(@RequestParam String writer) {
        PlanSetDTO planSetDTO = planService.LastReadOne(writer);
        return ResponseEntity.ok(planSetDTO);
    }

    // 일정표에서 등록된 장소 조회
    @ApiOperation(value = "Read PlanSet", notes = "Get 방식으로 등록 일정 조회")
    @GetMapping("/{planNo}")
    public ResponseEntity<PlanSetDTO> InitGET(@PathVariable Long planNo) {
        PlanSetDTO planSetDTO = planService.InitReadOne(planNo);
        return ResponseEntity.ok(planSetDTO);
    }

    // 찜목록에서 가져와 일정표에 넣기
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @PostMapping(value = "/{planNo}/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Long>>> registerPlanPlaceAdd(@RequestBody PlanPlaceBodyDTO planPlaceBodyDTO,
            @PathVariable Long planNo) {
        List<Long> ppOrdList = new ArrayList<>();

        StoreDTO storeDTO = storeService.read(planPlaceBodyDTO.getSno());

        PlanSetDTO planSetDTO = planService.InitReadOne(planNo);

        String Address = storeDTO.getP_address();

        var search = new GetXYRequest();

        search.setQuery(Address);

        var result = planService.getXY(search);

        GetXYResponse.Address firstAddress = result.getAddresses().get(0);

        Double mapx = Double.parseDouble(firstAddress.getX());
        Double mapy = Double.parseDouble(firstAddress.getY());

        log.info("mapx: {}, mapy: {}", mapx, mapy);

        PlanPlace planPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);

        PlanPlace planplace;

        PlanPlace LastPlanPlace = null;
        // 장소 저장
        // 이전에 등록된 장소가 없을 경우
        if (planPlace == null) {
            // 출발일 == (출발일시 + 머무는 시간)
            if (planSetDTO.getPs_startDate().toLocalDate().isEqual(planSetDTO.getPs_startDate()
                    .plusHours(planPlaceBodyDTO.getTakeTime().getHour())
                    .plusMinutes(planPlaceBodyDTO.getTakeTime().getMinute())
                    .plusSeconds(planPlaceBodyDTO.getTakeTime().getSecond()).toLocalDate())) {
                planplace = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address) // planReposi
                        .pp_startDate(planSetDTO.getPs_startDate()) // planReposi
                        .pp_takeDate(planPlaceBodyDTO.getTakeTime()) // storeReposi
                        .pp_mapx(mapx) // storeReposi
                        .pp_mapy(mapy) // storeReposi
                        .planSet(new PlanSet(planNo)) // planReposi
                        .pp_NightToNight((byte) 0)
                        .build();
                log.info("planplace 초기화 확인 : {}", planplace);

                ppOrdList.add(planPlaceRepository.save(planplace).getPpOrd());

            } else {
                LocalTime endTime = LocalTime.of(0, 0, 0);

                // 23:59:59 - 출발시간
                LocalTime prevTime = endTime.minusHours(planSetDTO.getPs_startDate().getHour())
                        .minusMinutes(planSetDTO.getPs_startDate().getMinute())
                        .minusSeconds(planSetDTO.getPs_startDate().getSecond());
                // 머무는 시간 - prevTime - 1초
                LocalTime nextTime = planPlaceBodyDTO.getTakeTime().minusHours(prevTime.getHour())
                        .minusMinutes(prevTime.getMinute()).minusSeconds(prevTime.getSecond()).minusSeconds(1);

                PlanPlace planplace1 = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address) // planReposi
                        .pp_startDate(planSetDTO.getPs_startDate()) // planReposi
                        .pp_takeDate(prevTime) // storeReposi
                        .pp_mapx(mapx) // storeReposi
                        .pp_mapy(mapy) // storeReposi
                        .planSet(new PlanSet(planNo)) // planReposi
                        .pp_NightToNight((byte) 1)
                        .build();
                ppOrdList.add(planPlaceRepository.save(planplace1).getPpOrd());

                PlanPlace planplace2 = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address) // planReposi
                        .pp_startDate(planSetDTO.getPs_startDate().plusDays(1).withHour(0).withMinute(0).withSecond(0)) // planReposi
                        .pp_takeDate(nextTime) // storeReposi
                        .pp_mapx(mapx) // storeReposi
                        .pp_mapy(mapy) // storeReposi
                        .planSet(new PlanSet(planNo)) // planReposi
                        .pp_NightToNight((byte) 2)
                        .build();
                ppOrdList.add(planPlaceRepository.save(planplace2).getPpOrd());
            }
            return ResponseEntity.ok(Map.of("ppOrd", ppOrdList));
        } else {
        //두번째 이상의 저장일 경우
            Map<String, Object> timeResult = planService.startTime(planNo, Address, mapx, mapy, planSetDTO.getWriter());
            // Map에서 꺼낼 때 필요한 형으로 캐스팅
            LocalDateTime startTime = (LocalDateTime) timeResult.get("pp_startDate");
            Integer getTNumber = (Integer) timeResult.get("getTNumber");

            // 장소 저장
            if (startTime.toLocalDate().isEqual(startTime
                    .plusHours(planPlaceBodyDTO.getTakeTime().getHour())
                    .plusMinutes(planPlaceBodyDTO.getTakeTime().getMinute())
                    .plusSeconds(planPlaceBodyDTO.getTakeTime().getSecond())
                    .toLocalDate())) {
                planplace = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address)
                        .pp_startDate(startTime)
                        .pp_takeDate(planPlaceBodyDTO.getTakeTime())
                        .pp_mapx(mapx)
                        .pp_mapy(mapy)
                        .planSet(new PlanSet(planNo))
                        .pp_NightToNight((byte) 0)
                        .build();

                log.info("planplace 초기화 확인 : {}", planplace);

                ppOrdList.add(planPlaceRepository.save(planplace).getPpOrd());
            } else {
                LocalTime endTime = LocalTime.of(0, 0, 0);

                // 23:59:59 - 출발시간
                LocalTime prevTime = endTime
                        .minusHours(startTime.getHour())
                        .minusMinutes(startTime.getMinute())
                        .minusSeconds(startTime.getSecond());

                // 머무는 시간 - prevTime
                LocalTime nextTime = planPlaceBodyDTO.getTakeTime()
                        .minusHours(prevTime.getHour())
                        .minusMinutes(prevTime.getMinute())
                        .minusSeconds(prevTime.getSecond());

                PlanPlace planplace1 = PlanPlace.builder()
                        .pp_title(storeDTO.getP_name())
                        .pp_startAddress(Address)
                        .pp_startDate(startTime)
                        .pp_takeDate(prevTime)
                        .pp_mapx(mapx)
                        .pp_mapy(mapy)
                        .planSet(new PlanSet(planNo))
                        .pp_NightToNight((byte) 1)
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
                        .pp_NightToNight((byte) 2)
                        .build();
                ppOrdList.add(planPlaceRepository.save(planplace2).getPpOrd());
            }

            // 외래키 지정

            log.info("현재 등록된 교통 수단의 갯수 : "+ getTNumber);
            if (getTNumber == 1) {
                // 최신 교통수단 저장내용 조회
                TransportParent LastTransportParent = transportParentRepository
                        .findLastTransportParent(planSetDTO.getWriter());
                // 마지막 저장 장소 조회
                LastPlanPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);
                // 교통수단의 장소 외래키 수정
                LastTransportParent.setPlanPlace(LastPlanPlace);
                // 교통수단 저장
                transportParentRepository.save(LastTransportParent);
            } else if (getTNumber == 2) {
                // 최신 교통수단 저장내용 조회
                List<TransportParent> LastTransportParent = transportParentRepository
                        .findLastTwoTransportParents(planSetDTO.getWriter());
                TransportParent LastTransportParent1 = LastTransportParent.get(0); // 가장 최신
                TransportParent LastTransportParent2 = LastTransportParent.get(1); // 두 번째 최신

                // 마지막 저장 장소 조회
                LastPlanPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);
                // 교통수단의 장소 외래키 수정
                LastTransportParent1.setPlanPlace(LastPlanPlace);
                // 교통수단 저장
                transportParentRepository.save(LastTransportParent1);
                // 교통수단의 장소 외래키 수정
                LastTransportParent2.setPlanPlace(LastPlanPlace);
                // 교통수단 저장
                transportParentRepository.save(LastTransportParent2);
            }
        }
        
        storeToPlanService.register(planPlaceBodyDTO.getSno(), LastPlanPlace);

        return ResponseEntity.ok(Map.of("ppOrd", ppOrdList));
    }

    // 일정표에서 등록된 장소 조회
    @ApiOperation(value = "Read PlanPlace", notes = "Get 방식으로 등록 장소 조회")
    @GetMapping("/{planNo}/planplaceAll")
    public ResponseEntity<List<PlanPlaceDTO>> getPlanPlaceList(@PathVariable Long planNo) {
        List<PlanPlaceDTO> planPlaceDTO = planService.listOfPlanPlaceAll(planNo);
        return ResponseEntity.ok(planPlaceDTO);
    }

    // 일정표에서 등록된 장소 조회
    @ApiOperation(value = "Read PlanPlace", notes = "Get 방식으로 등록 장소 조회")
    @GetMapping("/{planNo}/planplace")
    public ResponseEntity<List<PlanPlaceDTO>> getPlanPlace(@PathVariable Long planNo, @RequestParam Integer day) {
        List<PlanPlaceDTO> planPlaceDTO = planService.listOfPlanPlace(planNo, day);
        return ResponseEntity.ok(planPlaceDTO);
    }

    // 일정표에서 등록된 교통수단 조회
    @ApiOperation(value = "Read TransportParent", notes = "Get 방식으로 등록 이동수단 조회")
    @GetMapping("/{planNo}/TransportParent/{ppOrd}")
    public ResponseEntity<List<TransportParentDTO>> getTransportParent(@PathVariable Long planNo,
            @PathVariable Long ppOrd, @RequestParam Integer day) {
        List<TransportParentDTO> transportParentDTO = planService.listOfTransportParent(planNo, ppOrd, day);
        return ResponseEntity.ok(transportParentDTO);
    }

    // 일정표에서 등록된 교통 정보 조회
    @ApiOperation(value = "Read TransportChild", notes = "Get 방식으로 등록 교통정보 조회")
    @GetMapping("/{planNo}/TransportChild/{tno}")
    public ResponseEntity<List<TransportChildDTO>> getTransportChild(@PathVariable Long tno) {
        List<TransportChildDTO> transportChildDTO = planService.listOfTransportChild(tno);
        return ResponseEntity.ok(transportChildDTO);
    }

    @ApiOperation(value = "Delete PlanSet", notes = "DELETE 방식으로 일정표 삭제")
    @DeleteMapping(value = "/{planNo}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> removePlanSet(@PathVariable Long planNo) {
        try {
            planService.removePlanSet(planNo);

            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("planNo", planNo);

            return ResponseEntity.ok(resultMap);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping(value = "/{planNo}/planplace/{ppOrd}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> removePlanPlace(@PathVariable Long planNo, @PathVariable Long ppOrd) {
        try {
            planService.removePlanPlace(planNo, ppOrd);

            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("ppOrd", ppOrd);

            return ResponseEntity.ok(resultMap);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping(value = "/{planNo}/planplace/{ppOrd}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> updatePlanPlaceTime(@PathVariable Long planNo, @PathVariable Long ppOrd, @RequestBody PutPlanPlaceDTO putPlanPlaceDTO){
        try {
            log.info("버그찾기"+putPlanPlaceDTO.getTakeTime());
            planService.updatePlanPlaceTime(planNo, ppOrd, putPlanPlaceDTO.getTakeTime());

            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("ppOrd", ppOrd);

            return ResponseEntity.ok(resultMap);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
