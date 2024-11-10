package org.zerock.b01.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // 이 줄이 필요합니다.
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.TransportParentDTO;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.Search.DrivingRequest;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;
import org.zerock.b01.naver.NaverClient;
import org.zerock.b01.repository.PlanPlaceRepository;
import org.zerock.b01.repository.PlanRepository;
import org.zerock.b01.repository.TransportChildRepository;
import org.zerock.b01.repository.TransportParentRepository;
import org.zerock.b01.repository.function.PlanPlaceFunction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final ModelMapper modelMapper;

    private final PlanRepository planRepository;

    private final NaverClient naverClient;

    private final PlanPlaceRepository planPlaceRepository;

    private final TransportParentRepository transportParentRepository;

    private final TransportChildRepository transportChildRepository;

    private final ApiService apiService;

    private final PlanPlaceFunction planPlaceFunction;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverSecret;

    @Value("${naver.client.id2}")
    private String naverClientId2;

    @Value("${naver.client.secret2}")
    private String naverSecret2;

    @Value("${naver.url.search.local}")
    private String naverLocalSearchUrl;

    @Value("https://naveropenapi.apigw.gov-ntruss.com/map-direction-15/v1/driving")
    private String naverDrivingSearchUrl;

    @Override
    public PageResponseDTO<PlanSetDTO> list(PageRequestDTO pageRequestDTO) {

        Pageable pageable = pageRequestDTO.getPageable("planNo");

        Page<PlanSet> result = planRepository.findAll(pageable);

        List<PlanSetDTO> dtoList = result.getContent().stream()
                .map(place -> modelMapper.map(place,PlanSetDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<PlanSetDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }

    @Override
    public PlanSetDTO InitReadOne(Long planNo) {

        Optional<PlanSet> result = planRepository.findById(planNo);

        PlanSet plan = result.orElseThrow();

        PlanSetDTO planSetDTO = entityToDTO(plan);

        return planSetDTO;

    }

    @Override
    public PlanSetDTO LastReadOne(String writer) {

        Optional<PlanSet> result = Optional.ofNullable(planRepository.findLastPlanSetbyWriter(writer));

        PlanSet plan = result.orElseThrow();

        PlanSetDTO planSetDTO = entityToDTO(plan);

        return planSetDTO;


    }

    @Override
    public List<PlanPlaceDTO> listOfPlanPlaceAll(Long planNo) {

        List<PlanPlace> result = planPlaceRepository.findAllPlanNo(planNo);

        List<PlanPlaceDTO> dtoList = result.stream()
                .map(planPlace -> modelMapper.map(planPlace, PlanPlaceDTO.class)).collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public List<PlanPlaceDTO> listOfPlanPlace(Long planNo, Integer day) {

        LocalDate specificDate = planRepository.findById(planNo).get().getPs_startDate().plusDays(day - 1)
                .toLocalDate();

        LocalDateTime startOfday = specificDate.atStartOfDay();
        LocalDateTime endOfday = specificDate.atTime(LocalTime.MAX);

        List<PlanPlace> result = planPlaceFunction.findAllByDate(planNo, startOfday, endOfday);

        List<PlanPlaceDTO> dtoList = result.stream()
                .map(planPlace -> modelMapper.map(planPlace, PlanPlaceDTO.class)).collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public List<TransportParentDTO> listOfTransportParent(Long planNo, Long ppOrd, Integer day) {

        List<TransportParent> result = transportParentRepository.findByPpord(ppOrd);

        LocalDate specificDate = planRepository.findById(planNo).get().getPs_startDate().plusDays(day - 1)
                .toLocalDate();
        LocalDateTime startOfDay = specificDate.atStartOfDay();
        LocalDateTime endOfDay = specificDate.atTime(LocalTime.MAX);

        List<TransportParentDTO> dtoList = result.stream()
                .filter(transportParent -> {
                    LocalDateTime transportDate = transportParent.getT_goalDateTime();
                    return !transportDate.isBefore(startOfDay) && !transportDate.isAfter(endOfDay);
                })
                .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class))
                .collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public List<TransportChildDTO> listOfTransportChild(Long tno) {

        List<TransportChild> result = transportChildRepository.findByTno(tno);

        List<TransportChildDTO> dtoList = result.stream()
                .map(transportChild -> modelMapper.map(transportChild, TransportChildDTO.class))
                .collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public Long registerInit(PlanSetDTO planSetDTO) {

        PlanSet planSet = dtoToEntity(planSetDTO);

        Long planNo = planRepository.save(planSet).getPlanNo();

        return planNo;
    }

    private void saveTransportParent2(TransportParentDTO transportParentDTOPrev) {
        TransportParentDTO transportParentDTOPrev1 = transportParentDTOPrev;
        transportParentDTOPrev1.setT_goalDateTime(transportParentDTOPrev.getT_startDateTime()
                .withHour(23)
                .withMinute(59)
                .withSecond(59));
        PlanPlace planPlace1 = planPlaceRepository.findById(transportParentDTOPrev1.getPpOrd()).get();

        TransportParent transportParentNext1 = TransportParent.builder()
                .tno(transportParentDTOPrev1.getTno())
                .t_method(transportParentDTOPrev1.getT_method())
                .isCar(transportParentDTOPrev1.getIsCar())
                .t_goalDateTime(transportParentDTOPrev1.getT_goalDateTime())
                .t_startDateTime(transportParentDTOPrev1.getT_startDateTime())
                .t_takeTime(transportParentDTOPrev1.getT_takeTime())
                .writer(transportParentDTOPrev1.getWriter())
                .tp_NightToNight((byte) 1)
                .planPlace(planPlace1)
                .build();
        transportParentRepository.save(transportParentNext1);

        planPlaceFunction.updateTnoKey(transportParentDTOPrev1.getTno() + 1);

        TransportParentDTO transportParentDTOPrev2 = transportParentDTOPrev;
        transportParentDTOPrev2.setT_startDateTime(transportParentDTOPrev.getT_goalDateTime()
                .withHour(0)
                .withMinute(0)
                .withSecond(0));
        PlanPlace planPlace2 = planPlaceRepository.findById(transportParentDTOPrev1.getPpOrd()).get();

        TransportParent transportParentNext2 = TransportParent.builder()
                .tno(transportParentDTOPrev2.getTno() + 1)
                .t_method(transportParentDTOPrev2.getT_method())
                .isCar(transportParentDTOPrev2.getIsCar())
                .t_goalDateTime(transportParentDTOPrev2.getT_goalDateTime())
                .t_startDateTime(transportParentDTOPrev2.getT_startDateTime())
                .t_takeTime(transportParentDTOPrev2.getT_takeTime())
                .writer(transportParentDTOPrev2.getWriter())
                .tp_NightToNight((byte) 2)
                .planPlace(planPlace2)
                .build();
        transportParentRepository.save(transportParentNext2);
        log.info("뉴뉴정보" + transportParentNext2.getTno());
    }

    private void savePlanPlace2(PlanPlaceDTO planPlaceDTO, PlanPlaceDTO planPlaceDTON, Long planNo) {
        // 이전 장소의 출발 날짜 != 다음 장소의 출발 날짜
        // planPlacDTO.toLocalDate() != planPlaceDTON.toLocalDate()
        LocalTime endTime = LocalTime.of(0, 0, 0);

        // prevTime = 00:00:00 - 현재 요소의 출발시간
        LocalTime prevTime = endTime
                .minusHours(planPlaceDTO.getPp_startDate().getHour())
                .minusMinutes(planPlaceDTO.getPp_startDate().getMinute())
                .minusSeconds(planPlaceDTO.getPp_startDate().getSecond());

        // 머무는 시간 - prevTime
        LocalTime nextTime = planPlaceDTO.getPp_takeDate()
                .minusHours(prevTime.getHour())
                .minusMinutes(prevTime.getMinute())
                .minusSeconds(prevTime.getSecond());

        PlanPlace planplace1 = PlanPlace.builder()
                .ppOrd(planPlaceDTO.getPpOrd())
                .pp_title(planPlaceDTO.getPp_title())
                .pp_startAddress(planPlaceDTO.getPp_startAddress()) // planReposi
                .pp_startDate(planPlaceDTO.getPp_startDate()) // planReposi
                .pp_takeDate(prevTime) // storeReposi
                .pp_mapx(planPlaceDTO.getPp_mapx()) // storeReposi
                .pp_mapy(planPlaceDTO.getPp_mapy()) // storeReposi
                .planSet(new PlanSet(planNo)) // planReposi
                .pp_NightToNight((byte) 1)
                .build();

        planPlaceRepository.save(planplace1).getPpOrd();

        planPlaceFunction.updatePpOrdKey(planPlaceDTO.getPpOrd() + 1);

        PlanPlace planplace2 = PlanPlace.builder()
                .ppOrd(planPlaceDTO.getPpOrd() + 1)
                .pp_title(planPlaceDTO.getPp_title())
                .pp_startAddress(planPlaceDTO.getPp_startAddress()) // planReposi
                .pp_startDate(planPlaceDTON.getPp_startDate().withHour(0).withMinute(0).withSecond(0)) // planReposi
                .pp_takeDate(nextTime) // storeReposi
                .pp_mapx(planPlaceDTO.getPp_mapx()) // storeReposi
                .pp_mapy(planPlaceDTO.getPp_mapy()) // storeReposi
                .planSet(new PlanSet(planNo)) // planReposi
                .pp_NightToNight((byte) 2)
                .build();

        Long di = planPlaceRepository.save(planplace2).getPpOrd();

        j += 1;

        log.info("두번째 저장"+di);
    }

    @Override
    public GetXYResponse getXY(GetXYRequest getXYRequest) {
        try {
            String query = URLEncoder.encode(getXYRequest.getQuery(), StandardCharsets.UTF_8);
            String apiURL = naverLocalSearchUrl + "?query=" + query;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", naverClientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", naverSecret);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            log.info("버그 체크" + jsonResponse);
            JSONArray addresses = jsonResponse.getJSONArray("addresses");

            GetXYResponse result = new GetXYResponse();
            List<GetXYResponse.Address> addressList = new ArrayList<>();

            for (int i = 0; i < addresses.length(); i++) {
                JSONObject address = addresses.getJSONObject(i);
                GetXYResponse.Address addressObj = new GetXYResponse.Address(
                        address.getString("x"),
                        address.getString("y"));
                addressList.add(addressObj);
            }

            result.setAddresses(addressList);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("지역 검색 실패", e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> startTime(Long planNo, String Address, Double mapx, Double mapy, String writer) {
        // 자차 여부 조회
        PlanSetDTO planSetDTO = InitReadOne(planNo);
        Boolean isCar = planSetDTO.getIsCar();

        // 마지막 저장 장소 조회
        PlanPlace LastPlanPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);

        // DTO로 변환해서 꺼내쓰기
        PlanPlaceDTO LastPlanPlaceDTO = PlanPlaceDTO.builder()
                .pp_startDate(LastPlanPlace.getPp_startDate())
                .ppOrd(LastPlanPlace.getPpOrd())
                .pp_startAddress(LastPlanPlace.getPp_startAddress())
                .pp_takeDate(LastPlanPlace.getPp_takeDate())
                .pp_mapx(LastPlanPlace.getPp_mapx())
                .pp_mapy(LastPlanPlace.getPp_mapy())
                .planNo(LastPlanPlace.getPlanSet().getPlanNo())
                .pp_NightToNight(LastPlanPlace.getPp_NightToNight())
                .build();
        if (!LastPlanPlace.getPp_startAddress().equals(Address)) {
            Double pp_mapx = LastPlanPlaceDTO.getPp_mapx();
            Double pp_mapy = LastPlanPlaceDTO.getPp_mapy();

            // 출발 날짜, 시간
            LocalDateTime pp_startDate = LastPlanPlaceDTO.getPp_startDate();
            // 머무는 시간
            LocalTime pp_takeDate = LastPlanPlaceDTO.getPp_takeDate();

            // 출발 시간 = 출발 시간 + 머무는 시간
            pp_startDate = pp_startDate.plusHours(pp_takeDate.getHour())
                    .plusMinutes(pp_takeDate.getMinute())
                    .plusSeconds(pp_takeDate.getSecond());

            Integer getTNumber = 0;
            // 도착 장소 조회
            if (isCar == true) {
                try {
                    var request = new DrivingRequest();
                    request.setStart(pp_mapx + "," + pp_mapy);
                    request.setGoal(mapx + "," + mapy);

                    log.info("출발 좌표" + request.getStart());
                    log.info("도착 좌표" + request.getGoal());

                    var result = naverClient.Driving(request);
                    // 밀리초를 초로 변환
                    Integer duration = result.getRoute().getTraoptimal()[0].getSummary().getDuration();
                    long durationSeconds = duration / 1000; // 밀리초를 초로 변환

                    // 시, 분, 초 계산
                    int t_takeHour = (int) (durationSeconds / 3600); // 전체 시간을 시로 변환
                    int t_takeMinute = (int) ((durationSeconds % 3600) / 60); // 남은 초를 분으로 변환
                    int t_takeSecond = (int) (durationSeconds % 60); // 남은 초
                    // 출발시간 + 노는시간 + 이동시간

                    pp_startDate = pp_startDate.plusHours(t_takeHour);
                    pp_startDate = pp_startDate.plusMinutes(t_takeMinute);
                    pp_startDate = pp_startDate.plusSeconds(t_takeSecond);
                    LocalTime pp_takeTime = LocalTime.of(t_takeHour, t_takeMinute, t_takeSecond);

                    // 출발일 = 도착일 같을 시 하나만 생성
                    if (LastPlanPlaceDTO.getPp_startDate().toLocalDate().isEqual(pp_startDate.toLocalDate())) {
                        TransportParent transportParent = TransportParent.builder()
                                .isCar(true)
                                .t_method("차")
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate()
                                        .plusHours(pp_takeDate.getHour())
                                        .plusMinutes(pp_takeDate.getMinute())
                                        .plusSeconds(pp_takeDate.getSecond()))
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(pp_startDate)
                                .writer(writer)
                                .tp_NightToNight((byte) 0)
                                .build();

                        transportParentRepository.save(transportParent);
                        getTNumber = 1;
                    } else {
                        // 출발 시간 = 도착 시간 다를 시 두개 생성
                        LocalDateTime finalTime = LastPlanPlaceDTO.getPp_startDate().withHour(23).withMinute(59)
                                .withSecond(59);
                        TransportParent transportParent1 = TransportParent.builder()
                                .isCar(true)
                                .t_method("차")
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate()
                                        .plusHours(pp_takeDate.getHour())
                                        .plusMinutes(pp_takeDate.getMinute())
                                        .plusSeconds(pp_takeDate.getSecond()))
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(finalTime)
                                .writer(writer)
                                .tp_NightToNight((byte) 1)
                                .build();

                        transportParentRepository.save(transportParent1);

                        TransportParent transportParent2 = TransportParent.builder()
                                .isCar(true)
                                .t_method("차")
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate().plusDays(1).withHour(0)
                                        .withMinute(0).withSecond(0))
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(pp_startDate)
                                .writer(writer)
                                .tp_NightToNight((byte) 2)
                                .build();
                        transportParentRepository.save(transportParent2);

                        getTNumber = 2;
                    }
                } catch (Exception e) {
                    try {
                        System.out.println("오류메세지 : " + e);
                        TransTimeDTO transTimeDTO = TransTimeDTO.builder()
                                .t_startdatetime(pp_startDate)
                                .start_location(LastPlanPlace.getPp_startAddress())
                                .arrive_location(Address)
                                .isCar(true)
                                .build();

                        String result = apiService.callTransportApi("http://localhost:8000/plan/transport/add",
                                transTimeDTO);
                        // 최신 교통수단 저장내용 조회
                        TransportParent LastTransportParent = transportParentRepository.findLastTransportParent(writer);
                        // 출발시간 지정
                        pp_startDate = LastTransportParent.getT_goalDateTime();
                        // Python 스크립트의 응답을 파싱합니다.
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonNode jsonNode = mapper.readTree(result);
                            String getTNumberString = jsonNode.get("getTNumber").asText();
                            getTNumber = Integer.valueOf(getTNumberString);
                        } catch (Exception ex) {
                            log.error("Error parsing JSON response", ex);
                        }
                    } catch (Exception ex) {
                        System.out.println("오류메세지 : " + ex);
                        TransTimeDTO transTimeDTO = TransTimeDTO.builder()
                                .t_startdatetime(pp_startDate)
                                .start_location(LastPlanPlace.getPp_startAddress())
                                .arrive_location(Address)
                                .isCar(false)
                                .build();

                        String result = apiService.callTransportApi("http://localhost:8000/plan/transport/add",
                                transTimeDTO);
                        // 최신 교통수단 저장내용 조회
                        TransportParent LastTransportParent = transportParentRepository.findLastTransportParent(writer);
                        // 출발시간 지정
                        pp_startDate = LastTransportParent.getT_goalDateTime();
                        // Python 스크립트의 응답을 파싱합니다.
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            JsonNode jsonNode = mapper.readTree(result);
                            String getTNumberString = jsonNode.get("getTNumber").asText();
                            getTNumber = Integer.valueOf(getTNumberString);
                        } catch (Exception ext) {
                            log.error("Error parsing JSON response", ext);
                        }
                    }
                }
            } else {
                log.info("출발시간 : " + pp_startDate);
                TransTimeDTO transTimeDTO = TransTimeDTO.builder()
                        .writer(writer)
                        .t_startdatetime(pp_startDate)
                        .start_location(LastPlanPlace.getPp_startAddress())
                        .arrive_location(Address)
                        .isCar(false)
                        .build();
                log.info("타입: " + transTimeDTO.getT_startdatetime().getClass().getName() + "내용"
                        + transTimeDTO.getT_startdatetime());
                String result = apiService.callTransportApi("http://localhost:8000/plan/transport/add", transTimeDTO);

                // 최신 저장내용 조회
                TransportParent LastTransportParent = transportParentRepository.findLastTransportParent(writer);
                // 출발시간 지정
                pp_startDate = LastTransportParent.getT_goalDateTime();
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = mapper.readTree(result);
                    String getTNumberString = jsonNode.get("getTNumber").asText();
                    getTNumber = Integer.valueOf(getTNumberString);
                } catch (Exception ex) {
                    log.error("Error parsing JSON response", ex);
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("pp_startDate", pp_startDate);
            result.put("getTNumber", getTNumber);
            return result; // 반환
        } else {
            // 마지막 저장 장소가 이전과 같을 시에
            Map<String, Object> result = new HashMap<>();
            // 마지막 저장 장소 시작 일시 + 머무는 시간
            result.put("pp_startDate",
                    LastPlanPlaceDTO.getPp_startDate()
                            .plusHours(LastPlanPlaceDTO.getPp_takeDate().getHour())
                            .plusMinutes(LastPlanPlaceDTO.getPp_takeDate().getMinute())
                            .plusSeconds(LastPlanPlaceDTO.getPp_takeDate().getSecond()));
            result.put("getTNumber", 0);
            return result; // 반환
        }
    }
    // 시작 장소 조회

    @Override
    public void removePlanSet(Long planNo) {
        planRepository.deleteById(planNo);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removePlanPlace(Long planNo, Long ppOrd) {
        // 장소 정보 가져오기 및 변환
        // loseTime = 머무는 시간
        Optional<PlanPlace> planPlace1 = planPlaceRepository.findById(ppOrd);

        PlanPlaceDTO planPlaceDTO1 = modelMapper.map(planPlace1, PlanPlaceDTO.class);

        LocalTime loseTime = planPlaceDTO1.getPp_takeDate();

        if (planPlaceDTO1.getPp_NightToNight() == 0) {
            planPlaceRepository.deleteById(ppOrd);
        } else if (planPlaceDTO1.getPp_NightToNight() == 1) {
            Optional<PlanPlace> planPlacedaum = Optional.ofNullable(planPlaceRepository.findNextId(planNo, ppOrd));
            PlanPlaceDTO planPlaceDTOdaum = modelMapper.map(planPlacedaum, PlanPlaceDTO.class);
            loseTime = loseTime.plusHours(planPlaceDTOdaum.getPp_takeDate().getHour())
                    .plusMinutes(planPlaceDTOdaum.getPp_takeDate().getMinute())
                    .plusSeconds(planPlaceDTOdaum.getPp_takeDate().getSecond())
                    .plusSeconds(1);
            planPlaceRepository.deleteById(ppOrd);
            planPlaceRepository.deleteById(planPlaceDTOdaum.getPpOrd());
        } else {
            Optional<PlanPlace> planPlaceprev = Optional.ofNullable(planPlaceRepository.findPrevId(planNo, ppOrd));
            PlanPlaceDTO planPlaceDTOprev = modelMapper.map(planPlaceprev, PlanPlaceDTO.class);
            loseTime = loseTime.plusHours(planPlaceDTOprev.getPp_takeDate().getHour())
                    .plusMinutes(planPlaceDTOprev.getPp_takeDate().getMinute())
                    .plusSeconds(planPlaceDTOprev.getPp_takeDate().getSecond())
                    .minusSeconds(1);
            planPlaceRepository.deleteById(ppOrd);
            planPlaceRepository.deleteById(planPlaceDTOprev.getPpOrd());
        }

        // 교통정보에서 시간 가져와서 loseTime에 더하기
        // loseTime = 머무는 시간 + 이동 시간
        List<TransportParent> transportParents = transportParentRepository.findByPpord(ppOrd);

        List<TransportParentDTO> transportParentDTOS = transportParents.stream()
                .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class))
                .collect(Collectors.toList());

        for (TransportParentDTO transportParentDTO : transportParentDTOS) {
            loseTime = loseTime.plusHours(transportParentDTO.getT_takeTime().getHour())
                    .plusMinutes(transportParentDTO.getT_takeTime().getMinute())
                    .plusSeconds(transportParentDTO.getT_takeTime().getSecond());
        }

        if (transportParentDTOS.size() == 2) {
            loseTime = loseTime.plusSeconds(1);
        }

        // 지정된 장소 이후의 장소 정보 가져오기
        List<PlanPlace> planPlaces = planPlaceRepository.findAllAfterId(planNo, ppOrd);

        List<PlanPlaceDTO> planPlaceDTOS = planPlaces.stream()
                .map(planPlace -> modelMapper.map(planPlace, PlanPlaceDTO.class)).collect(Collectors.toList());

        // 순환 참조 가능한 리스트로 전환
        ListIterator<PlanPlaceDTO> PlanPlaceIterator = planPlaceDTOS.listIterator();

        while (PlanPlaceIterator.hasNext()) {
            PlanPlaceDTO planPlaceDTO = PlanPlaceIterator.next();

            // NightToNight 1, 2 통합
            if (planPlaceDTO.getPp_NightToNight() == 1) {
                if (PlanPlaceIterator.hasNext()) {
                    PlanPlaceDTO planPlaceDTO2 = PlanPlaceIterator.next();
                    // 부모교통정보는 2번째 ppOrd를 참조하기 때문
                    planPlaceDTO.setPpOrd(planPlaceDTO2.getPpOrd());
                    // NightToNight가 1인 경우 NightToNight = 1이 오늘 보내는 시간 + 내일 보내는 시간 + 1초
                    planPlaceDTO.setPp_takeDate(
                            planPlaceDTO.getPp_takeDate().plusHours(planPlaceDTO2.getPp_takeDate().getHour())
                                    .plusMinutes(planPlaceDTO2.getPp_takeDate().getMinute())
                                    .plusSeconds(planPlaceDTO2.getPp_takeDate().getSecond()).plusSeconds(1));
                    planPlaceDTO.setPp_NightToNight((byte) 0);
                    // NightToNight가 2인 것을 제거
                    PlanPlaceIterator.remove();
                }
            }

            // 모든 planPlace 데이터를 - loseTime 해주기
            planPlaceDTO.setPp_startDate(planPlaceDTO.getPp_startDate()
                    .minusHours(loseTime.getHour())
                    .minusMinutes(loseTime.getMinute())
                    .minusSeconds(loseTime.getSecond()));

            // 교통 정보 조회
            List<TransportParent> TransportParents = transportParentRepository.findByPpord(planPlaceDTO.getPpOrd());

            List<TransportParentDTO> transportParentDTOList = TransportParents.stream()
                    .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class))
                    .collect(Collectors.toList());

            // 모든 교통 정보 - loseTime
            for (TransportParentDTO transportParentDTO : transportParentDTOList) {
                transportParentDTO
                        .setT_startDateTime(transportParentDTO.getT_startDateTime()
                                .minusHours(loseTime.getHour())
                                .minusMinutes(loseTime.getMinute())
                                .minusSeconds(loseTime.getSecond()));

                transportParentDTO
                        .setT_goalDateTime(transportParentDTO.getT_goalDateTime()
                                .minusHours(loseTime.getHour())
                                .minusMinutes(loseTime.getMinute())
                                .minusSeconds(loseTime.getSecond()));
            }

            // 장소 더하기
            // 교통정보가 2개일 경우
            if (transportParentDTOList.size() == 2) {
                TransportParentDTO transportParentDTOPrev = TransportParentDTO.builder()
                        .tno(transportParentDTOList.get(0).getTno())
                        .ppOrd(transportParentDTOList.get(0).getPpOrd())
                        .isCar(transportParentDTOList.get(0).getIsCar()) // planReposi
                        .t_method(transportParentDTOList.get(0).getT_method()) // planReposi
                        .t_startDateTime(transportParentDTOList.get(0).getT_startDateTime()) // storeReposi
                        .t_takeTime(transportParentDTOList.get(0).getT_takeTime()
                                .plusHours(transportParentDTOList.get(1).getT_takeTime().getHour())
                                .plusMinutes(transportParentDTOList.get(1).getT_takeTime().getMinute())
                                .plusSeconds(transportParentDTOList.get(1).getT_takeTime().getSecond())
                                .plusSeconds(1)) // storeReposi
                        .t_goalDateTime(transportParentDTOList.get(1).getT_goalDateTime()) // storeReposi
                        .writer(transportParentDTOList.get(0).getWriter()) // planReposi
                        .tp_NightToNight((byte) 0)
                        .build();

                if (transportParentDTOPrev.getT_startDateTime().toLocalDate()
                        .isEqual(transportParentDTOPrev.getT_goalDateTime().toLocalDate())) {
                    PlanPlace planPlaceprev = planPlaceRepository.findById(transportParentDTOPrev.getPpOrd()).get();

                    TransportParent transportParent = TransportParent.builder()
                            .tno(transportParentDTOPrev.getTno())
                            .t_method(transportParentDTOPrev.getT_method())
                            .isCar(transportParentDTOPrev.getIsCar())
                            .t_goalDateTime(transportParentDTOPrev.getT_goalDateTime())
                            .t_startDateTime(transportParentDTOPrev.getT_startDateTime())
                            .t_takeTime(transportParentDTOPrev.getT_takeTime())
                            .writer(transportParentDTOPrev.getWriter())
                            .tp_NightToNight(transportParentDTOPrev.getTp_NightToNight())
                            .planPlace(planPlaceprev)
                            .build();

                    transportParentRepository.save(transportParent);
                    // 출발 시간 != 도착시간
                } else {
                    saveTransportParent2(transportParentDTOPrev);
                }
            } else {
                TransportParentDTO transportParentDTOPrev = TransportParentDTO.builder()
                        .tno(transportParentDTOList.get(0).getTno())
                        .ppOrd(transportParentDTOList.get(0).getPpOrd())
                        .isCar(transportParentDTOList.get(0).getIsCar())
                        .t_method(transportParentDTOList.get(0).getT_method())
                        .t_startDateTime(transportParentDTOList.get(0).getT_startDateTime())
                        .t_takeTime((transportParentDTOList.get(0).getT_takeTime()))
                        .t_goalDateTime(transportParentDTOList.get(0).getT_goalDateTime())
                        .writer(transportParentDTOList.get(0).getWriter())
                        .tp_NightToNight((byte) 0)
                        .build();

                if (transportParentDTOPrev.getT_startDateTime().toLocalDate()
                        .isEqual(transportParentDTOPrev.getT_goalDateTime().toLocalDate())) {
                    PlanPlace planPlacePrev = planPlaceRepository.findById(transportParentDTOPrev.getPpOrd()).get();

                    TransportParent transportParent = TransportParent.builder()
                            .tno(transportParentDTOPrev.getTno())
                            .t_method(transportParentDTOPrev.getT_method())
                            .isCar(transportParentDTOPrev.getIsCar())
                            .t_goalDateTime(transportParentDTOPrev.getT_goalDateTime())
                            .t_startDateTime(transportParentDTOPrev.getT_startDateTime())
                            .t_takeTime(transportParentDTOPrev.getT_takeTime())
                            .writer(transportParentDTOPrev.getWriter())
                            .tp_NightToNight(transportParentDTOPrev.getTp_NightToNight())
                            .planPlace(planPlacePrev)
                            .build();
                    transportParentRepository.save(transportParent);
                    // 출발 시간 != 도착시간
                } else {
                    saveTransportParent2(transportParentDTOPrev);
                }
            }

            if (PlanPlaceIterator.hasNext()) {
                PlanPlaceDTO planPlaceDTON = PlanPlaceIterator.next();
                planPlaceDTON.setPp_startDate(planPlaceDTON.getPp_startDate()
                        .minusHours(loseTime.getHour())
                        .minusMinutes(loseTime.getMinute())
                        .minusSeconds(loseTime.getSecond()));

                // 이전 장소의 출발 날짜 == 다음 장소의 출발 날짜
                if (planPlaceDTO.getPp_startDate().toLocalDate()
                        .isEqual(planPlaceDTON.getPp_startDate().toLocalDate())) {
                    PlanSet planSet = planRepository.findById(planPlaceDTO.getPlanNo()).get();

                    PlanPlace planPlaced = PlanPlace.builder()
                            .ppOrd(planPlaceDTO.getPpOrd())
                            .pp_title(planPlaceDTO.getPp_title())
                            .pp_startAddress(planPlaceDTO.getPp_startAddress())
                            .pp_startDate(planPlaceDTO.getPp_startDate())
                            .pp_takeDate(planPlaceDTO.getPp_takeDate())
                            .pp_mapx(planPlaceDTO.getPp_mapx())
                            .pp_mapy(planPlaceDTO.getPp_mapy())
                            .pp_NightToNight(planPlaceDTO.getPp_NightToNight())
                            .planSet(planSet)
                            .build();

                    planPlaceRepository.save(planPlaced);
                } else {
                    savePlanPlace2(planPlaceDTO, planPlaceDTON, planNo);
                }
            }
        }
        planPlaceRepository.deleteById(ppOrd);
    }

    int j = 0;
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updatePlanPlaceTime(Long planNo, Long ppOrd, LocalTime takeTime) {
        // 장소 정보 가져오기 및 변환
        // loseTime = 머무는 시간
        Optional<PlanPlace> planPlace1 = planPlaceRepository.findById(ppOrd);

        PlanPlaceDTO planPlaceDTO1 = modelMapper.map(planPlace1, PlanPlaceDTO.class);

        planPlaceDTO1.setPp_takeDate(takeTime);

        PlanSet planSet1 = planRepository.findById(planPlaceDTO1.getPlanNo()).get();

        PlanPlace updatePlanPlace = PlanPlace.builder()
                .ppOrd(planPlaceDTO1.getPpOrd())
                .pp_title(planPlaceDTO1.getPp_title())
                .pp_startAddress(planPlaceDTO1.getPp_startAddress())
                .pp_startDate(planPlaceDTO1.getPp_startDate())
                .pp_takeDate(planPlaceDTO1.getPp_takeDate())
                .pp_mapx(planPlaceDTO1.getPp_mapx())
                .pp_mapy(planPlaceDTO1.getPp_mapy())
                .pp_NightToNight(planPlaceDTO1.getPp_NightToNight())
                .planSet(planSet1)
                .build();

        planPlaceRepository.save(updatePlanPlace);

        log.info("버그 찾기1" + planPlaceDTO1);

        Duration duration = Duration.between(planPlaceDTO1.getPp_takeDate(),takeTime);
        int loseHour = (int) duration.toHours();
        int loseMinute = duration.toMinutesPart();
        int loseSecond = duration.toSecondsPart();

        log.info("시시" + loseHour);
        // 지정된 장소 이후의 장소 정보 가져오기
        List<PlanPlace> planPlaces = planPlaceRepository.findAllAfterId(planNo, ppOrd);

        List<PlanPlaceDTO> planPlaceDTOS = planPlaces.stream()
                .map(planPlace -> modelMapper.map(planPlace, PlanPlaceDTO.class)).collect(Collectors.toList());

        log.info("리스트 반환 : " + planPlaceDTOS);
        log.info("리스트 갯수 : " + planPlaceDTOS.size());

        // 지정된 장소 이후이므로 다음 장소
        for (int i = 0; i < planPlaceDTOS.size(); i++) {
            PlanPlaceDTO planPlaceDTO = planPlaceDTOS.get(i);
            log.info("이상무" + planPlaceDTO);

            // NightToNight 1, 2 통합
            // 현재 planplace가 1인경우
            if (planPlaceDTO.getPp_NightToNight() == 1) {
                PlanPlaceDTO planPlaceDTO2 = planPlaceDTOS.get(i + 1);
                // 부모교통정보는 2번째 ppOrd를 참조하기 때문
                planPlaceDTO.setPpOrd(planPlaceDTO2.getPpOrd());
                // NightToNight가 1인 경우 NightToNight = 1이 오늘 보내는 시간 + 내일 보내는 시간
                planPlaceDTO.setPp_takeDate(planPlaceDTO.getPp_takeDate()
                        .plusHours(planPlaceDTO2.getPp_takeDate().getHour())
                        .plusMinutes(planPlaceDTO2.getPp_takeDate().getMinute())
                        .plusSeconds(planPlaceDTO2.getPp_takeDate().getSecond()));
                log.info("시간 병합1" + planPlaceDTO.getPp_takeDate());
                planPlaceDTO.setPp_NightToNight((byte) 0);
                // NightToNight가 2인 것을 제거
            }

            // 모든 planPlace 데이터를 - loseTime 해주기
            planPlaceDTO.setPp_startDate(planPlaceDTO.getPp_startDate()
                    .minusHours(loseHour)
                    .minusMinutes(loseMinute)
                    .minusSeconds(loseSecond));

            log.info("시간 계산1" + planPlaceDTO.getPp_startDate());

            // 교통 정보 조회
            List<TransportParent> TransportParents = transportParentRepository.findByPpord(planPlaceDTO.getPpOrd());
            log.info("정정정보" + TransportParents);

            List<TransportParentDTO> transportParentDTOList = TransportParents.stream()
                    .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class))
                    .collect(Collectors.toList());
            log.info("정정보" + transportParentDTOList);
            // 모든 교통 정보 - loseTime
            List<TransportParentDTO> updatedTransportParentDTOs = new ArrayList<>();
            for (TransportParentDTO transportParentDTO : transportParentDTOList) {
                transportParentDTO
                        .setT_startDateTime(transportParentDTO.getT_startDateTime()
                                .minusHours(loseHour)
                                .minusMinutes(loseMinute)
                                .minusSeconds(loseSecond));

                transportParentDTO
                        .setT_goalDateTime(transportParentDTO.getT_goalDateTime()
                                .minusHours(loseHour)
                                .minusMinutes(loseMinute)
                                .minusSeconds(loseSecond));
                updatedTransportParentDTOs.add(transportParentDTO);
            }
            log.info("정보" + updatedTransportParentDTOs);

            // 장소 더하기
            // 교통정보가 2개일 경우 2개를 통합
            TransportParentDTO transportParentDTOPrev;
            log.info("리스트 요소" + updatedTransportParentDTOs);
            if (updatedTransportParentDTOs.size() == 2) {
                transportParentDTOPrev = TransportParentDTO.builder()
                        .tno(updatedTransportParentDTOs.get(0).getTno())
                        .ppOrd(updatedTransportParentDTOs.get(0).getPpOrd())
                        .isCar(updatedTransportParentDTOs.get(0).getIsCar()) // planReposi
                        .t_method(updatedTransportParentDTOs.get(0).getT_method()) // planReposi
                        .t_startDateTime(updatedTransportParentDTOs.get(0).getT_startDateTime()) // storeReposi
                        .t_takeTime(updatedTransportParentDTOs.get(0).getT_takeTime()
                                .plusHours(updatedTransportParentDTOs.get(1).getT_takeTime().getHour())
                                .plusMinutes(updatedTransportParentDTOs.get(1).getT_takeTime().getMinute())
                                .plusSeconds(updatedTransportParentDTOs.get(1).getT_takeTime().getSecond())
                                .plusSeconds(1)) // storeReposi
                        .t_goalDateTime(updatedTransportParentDTOs.get(1).getT_goalDateTime()) // storeReposi
                        .writer(updatedTransportParentDTOs.get(0).getWriter()) // planReposi
                        .tp_NightToNight((byte) 0)
                        .build();
            } else {
                // 교통 정보가 하나일 경우
                transportParentDTOPrev = TransportParentDTO.builder()
                        .tno(updatedTransportParentDTOs.get(0).getTno())
                        .ppOrd(updatedTransportParentDTOs.get(0).getPpOrd())
                        .isCar(updatedTransportParentDTOs.get(0).getIsCar())
                        .t_method(updatedTransportParentDTOs.get(0).getT_method())
                        .t_startDateTime(updatedTransportParentDTOs.get(0).getT_startDateTime())
                        .t_takeTime((updatedTransportParentDTOs.get(0).getT_takeTime()))
                        .t_goalDateTime(updatedTransportParentDTOs.get(0).getT_goalDateTime())
                        .writer(updatedTransportParentDTOs.get(0).getWriter())
                        .tp_NightToNight((byte) 0)
                        .build();
            }
            log.info("뉴정보" + transportParentDTOPrev);

            // 다시 분리
            if (transportParentDTOPrev.getT_startDateTime().toLocalDate()
                    .isEqual(transportParentDTOPrev.getT_goalDateTime().toLocalDate())) {
                PlanPlace planPlacePrev = planPlaceRepository.findById(transportParentDTOPrev.getPpOrd()).get();

                TransportParent transportParentNext = TransportParent.builder()
                        .tno(transportParentDTOPrev.getTno())
                        .t_method(transportParentDTOPrev.getT_method())
                        .isCar(transportParentDTOPrev.getIsCar())
                        .t_goalDateTime(transportParentDTOPrev.getT_goalDateTime())
                        .t_startDateTime(transportParentDTOPrev.getT_startDateTime())
                        .t_takeTime(transportParentDTOPrev.getT_takeTime())
                        .writer(transportParentDTOPrev.getWriter())
                        .tp_NightToNight((byte) 0)
                        .planPlace(planPlacePrev)
                        .build();

                Long tno = transportParentRepository.save(transportParentNext).getTno();
                log.info("뉴/뉴정보" + tno);
                // 출발 시간 != 도착시간
            } else {
                saveTransportParent2(transportParentDTOPrev);
            }
            // planPlaceDTON는 다다음 요소의 장소
            // 다다음 요소가 있을 경우
            if (i + 1 < planPlaceDTOS.size()) {
                PlanPlaceDTO planPlaceDTON = planPlaceDTOS.get(i + 1);

                planPlaceDTON.setPp_startDate(planPlaceDTON.getPp_startDate()
                        .minusHours(loseHour)
                        .minusMinutes(loseMinute)
                        .minusSeconds(loseSecond));

                // 이전 장소의 출발 날짜 == 다음 장소의 출발 날짜
                if (planPlaceDTO.getPp_startDate().toLocalDate()
                        .isEqual(planPlaceDTON.getPp_startDate().toLocalDate())) {
                    PlanSet planSet = planRepository.findById(planPlaceDTO.getPlanNo()).get();

                    PlanPlace planPlaced = PlanPlace.builder()
                            .ppOrd(planPlaceDTO.getPpOrd() + j)
                            .pp_title(planPlaceDTO.getPp_title())
                            .pp_startAddress(planPlaceDTO.getPp_startAddress())
                            .pp_startDate(planPlaceDTO.getPp_startDate())
                            .pp_takeDate(planPlaceDTO.getPp_takeDate())
                            .pp_mapx(planPlaceDTO.getPp_mapx())
                            .pp_mapy(planPlaceDTO.getPp_mapy())
                            .pp_NightToNight(planPlaceDTO.getPp_NightToNight())
                            .planSet(planSet)
                            .build();
                    log.info("뉴뉴뉴정보" + planPlaced.getPpOrd());
                    planPlaceRepository.save(planPlaced);

                } else {
                    savePlanPlace2(planPlaceDTO, planPlaceDTON, planNo);
                }
            } else {
                // 다음 요소가 하나뿐일 경우
                PlanSet planSet = planRepository.findById(planPlaceDTO.getPlanNo()).get();

                PlanPlace planPlaced = PlanPlace.builder()
                        .ppOrd(planPlaceDTO.getPpOrd() + j)
                        .pp_title(planPlaceDTO.getPp_title())
                        .pp_startAddress(planPlaceDTO.getPp_startAddress())
                        .pp_startDate(planPlaceDTO.getPp_startDate())
                        .pp_takeDate(planPlaceDTO.getPp_takeDate())
                        .pp_mapx(planPlaceDTO.getPp_mapx())
                        .pp_mapy(planPlaceDTO.getPp_mapy())
                        .pp_NightToNight(planPlaceDTO.getPp_NightToNight())
                        .planSet(planSet)
                        .build();
                planPlaceRepository.save(planPlaced);
            }
        }
    }
}
