package org.zerock.b01.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.domain.TransportChild;
import org.zerock.b01.domain.TransportParent;
import org.zerock.b01.dto.TransportParentDTO;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.Search.DrivingRequest;
import org.zerock.b01.dto.Search.DrivingResponse;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;
import org.zerock.b01.repository.PlanPlaceRepository;
import org.zerock.b01.repository.PlanRepository;
import org.zerock.b01.repository.TransportChildRepository;
import org.zerock.b01.repository.TransportParentRepository;
import org.zerock.b01.repository.function.PlanPlaceFunction;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private final PlanPlaceRepository planPlaceRepository;

    private final TransportParentRepository transportParentRepository;

    private final TransportChildRepository transportChildRepository;

    private final ApiService apiService;

    private final PlanPlaceFunction planPlaceFunction;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverSecret;

    @Value("${naver.url.search.local}")
    private String naverLocalSearchUrl;

    @Value("https://naveropenapi.apigw.gov-ntruss.com/map-direction-15/v1/driving")
    private String naverDrivingSearchUrl;

    @Override
    public PlanSetDTO InitReadOne(Long planNo) {

        Optional<PlanSet> result = planRepository.findById(planNo);

        PlanSet plan = result.orElseThrow();

        PlanSetDTO planSetDTO = entityToDTO(plan);

        return planSetDTO;
    }

    @Override
    public List<PlanPlaceDTO> listOfPlanPlace(Long planNo, Integer day) {

        LocalDate specificDate = planRepository.findById(planNo).get().getStartDate().plusDays(day - 1).toLocalDate();

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

        LocalDate specificDate = planRepository.findById(planNo).get().getStartDate().plusDays(day - 1).toLocalDate();
        LocalDateTime startOfDay = specificDate.atStartOfDay();
        LocalDateTime endOfDay = specificDate.atTime(LocalTime.MAX);

        List<TransportParentDTO> dtoList = result.stream()
                .filter(transportParent -> {
                    LocalDateTime transportDate = transportParent.getT_goalDateTime();
                    return !transportDate.isBefore(startOfDay) && !transportDate.isAfter(endOfDay);
                })
                .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class)).collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public List<TransportChildDTO> listOfTransportChild(Long tno) {

        List<TransportChild> result = transportChildRepository.findByTno(tno);

        List<TransportChildDTO> dtoList = result.stream()
                .map(transportChild -> modelMapper.map(transportChild, TransportChildDTO.class)).collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public Long registerInit(PlanSetDTO planSetDTO) {

        PlanSet planSet = dtoToEntity(planSetDTO);

        Long planNo = planRepository.save(planSet).getPlanNo();

        return planNo;
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
    public DrivingResponse getTime(DrivingRequest drivingRequest) {
        try {
            String start = URLEncoder.encode(drivingRequest.getStart(), StandardCharsets.UTF_8);
            String goal = URLEncoder.encode(drivingRequest.getGoal(), StandardCharsets.UTF_8);
            String apiURL = naverDrivingSearchUrl + "?start=" + start + "&goal=" + goal;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", naverClientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", naverSecret);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
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
            JSONObject routeObject = jsonResponse.getJSONObject("route");
            JSONArray traoptimalArray = routeObject.getJSONArray("traoptimal");
            JSONObject firstTraoptimal = traoptimalArray.getJSONObject(0);
            JSONObject summaryObject = firstTraoptimal.getJSONObject("summary");

            DrivingResponse result = new DrivingResponse();
            DrivingResponse.Route route = new DrivingResponse.Route();
            DrivingResponse.Route.Traoptimal traoptimal = new DrivingResponse.Route.Traoptimal();
            DrivingResponse.Route.Traoptimal.Summary summary = new DrivingResponse.Route.Traoptimal.Summary();

            summary.setDuration(summaryObject.getInt("duration"));
            traoptimal.setSummary(summary);
            route.setTraoptimal(new DrivingResponse.Route.Traoptimal[] { traoptimal });
            result.setRoute(route);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("경로 검색 실패", e);
        }

    }

    @Override
    public Map<String, Object> startTime(Long planNo, String Address, Float mapx, Float mapy, String writer) {
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
                .planNo(LastPlanPlace.getPlanSet())
                .NightToNight(LastPlanPlace.getNightToNight())
                .build();
        if (LastPlanPlace.getPp_startAddress() != Address) {
            Float pp_mapx = LastPlanPlaceDTO.getPp_mapx();
            Float pp_mapy = LastPlanPlaceDTO.getPp_mapy();

            // 출발 날짜, 시간
            LocalDateTime pp_startDate = LastPlanPlaceDTO.getPp_startDate();
            // 노는 시간
            LocalTime pp_takeDate = LastPlanPlaceDTO.getPp_takeDate();

            // 출발 시간 + 머무는 시간
            pp_startDate = pp_startDate.plusHours(pp_takeDate.getHour())
                    .plusMinutes(pp_takeDate.getMinute());

            Integer getTNumber = 0;
            // 도착 장소 조회
            if (isCar == true) {
                try {
                    var request = new DrivingRequest();
                    request.setStart(pp_mapx + "," + pp_mapy);
                    request.setGoal(mapx + "," + mapy);
                    var result = getTime(request);
                    Integer duration = result.getRoute().getTraoptimal()[0].getSummary().getDuration();
                    int t_takeHour = (int) ((duration % 86400) / 3600); // 1시간 = 3600초
                    int t_takeMinute = (int) ((duration % 3600) / 60); // 1분 = 60초
                    // 출발시간 + 노는시간 + 이동시간

                    pp_startDate = pp_startDate.plusHours(t_takeHour);
                    pp_startDate = pp_startDate.plusMinutes(t_takeMinute);
                    LocalTime pp_takeTime = LocalTime.of(t_takeHour, t_takeMinute);

                    // 출발알 = 도착일 같을 시 하나만 생성
                    if (LastPlanPlaceDTO.getPp_startDate().toLocalDate() == pp_startDate.toLocalDate()) {
                        TransportParent transportParent = TransportParent.builder()
                                .isCar(true)
                                .t_method("차")
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate())
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(pp_startDate)
                                .writer(writer)
                                .NightToNight((byte)0)
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
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate())
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(finalTime)
                                .writer(writer)
                                .NightToNight((byte)1)
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
                                .NightToNight((byte)2)
                                .build();
                        transportParentRepository.save(transportParent2);

                        getTNumber = 2;
                    }
                } catch (Exception e) {
                    System.out.println("오류메세지 : " + e);
                    TransTimeDTO transTimeDTO = TransTimeDTO.builder()
                            .t_startHour(pp_startDate.getHour())
                            .t_startMinute(pp_startDate.getMinute())
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
                    } catch (Exception ex) {
                        log.error("Error parsing JSON response", ex);
                    }
                }
            } else {
                TransTimeDTO transTimeDTO = TransTimeDTO.builder()
                        .writer(writer)
                        .t_startHour(pp_startDate.getHour())
                        .t_startMinute(pp_startDate.getMinute())
                        .start_location(LastPlanPlace.getPp_startAddress())
                        .arrive_location(Address)
                        .isCar(false)
                        .build();

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
            Map<String, Object> result = new HashMap<>();
            // 마지막 저장 장소 시작 일시 + 머무는 시간
            result.put("pp_startDate",
                    LastPlanPlaceDTO.getPp_startDate().plusHours(LastPlanPlaceDTO.getPp_takeDate().getHour())
                            .plusMinutes(LastPlanPlaceDTO.getPp_takeDate().getMinute()));
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
    public void removePlanPlace(Long planNo, Long ppOrd) {
        //장소 정보 가져오기 및 변환
        //loseTime = 머무는 시간
        Optional<PlanPlace> planPlace1 = planPlaceRepository.findById(ppOrd);

        PlanPlaceDTO planPlaceDTO1 = modelMapper.map(planPlace1, PlanPlaceDTO.class);

        LocalTime loseTime = planPlaceDTO1.getPp_takeDate();

        if(planPlaceDTO1.getNightToNight() == 0){
            planPlaceRepository.deleteById(ppOrd);
        } else if (planPlaceDTO1.getNightToNight() == 1) {
            Optional<PlanPlace> planPlacedaum = Optional.ofNullable(planPlaceRepository.findNextId(planNo, ppOrd));
            PlanPlaceDTO planPlaceDTOdaum = modelMapper.map(planPlacedaum, PlanPlaceDTO.class);
            loseTime = loseTime.plusHours(planPlaceDTOdaum.getPp_takeDate().getHour())
                    .plusMinutes(planPlaceDTOdaum.getPp_takeDate().getMinute())
                    .plusSeconds(planPlaceDTOdaum.getPp_takeDate().getSecond())
                    .plusSeconds(1);
            planPlaceRepository.deleteById(ppOrd);
            planPlaceRepository.deleteById(planPlaceDTOdaum.getPpOrd());
        } else{
            Optional<PlanPlace> planPlaceprev = Optional.ofNullable(planPlaceRepository.findPrevId(planNo, ppOrd));
            PlanPlaceDTO planPlaceDTOprev = modelMapper.map(planPlaceprev, PlanPlaceDTO.class);
            loseTime = loseTime.plusHours(planPlaceDTOprev.getPp_takeDate().getHour())
                    .plusMinutes(planPlaceDTOprev.getPp_takeDate().getMinute())
                    .plusSeconds(planPlaceDTOprev.getPp_takeDate().getSecond())
                    .minusSeconds(1);
            planPlaceRepository.deleteById(ppOrd);
            planPlaceRepository.deleteById(planPlaceDTOprev.getPpOrd());
        }

        //교통정보에서 시간 가져와서 loseTime에 더하기
        //loseTime = 머무는 시간 + 이동 시간
        List<TransportParent> transportParents = transportParentRepository.findByPpord(ppOrd);

        List<TransportParentDTO> transportParentDTOS = transportParents.stream()
                .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class)).collect(Collectors.toList());

        for (TransportParentDTO transportParentDTO : transportParentDTOS){
            loseTime = loseTime.plusHours(transportParentDTO.getT_takeTime().getHour())
                    .plusMinutes(transportParentDTO.getT_takeTime().getMinute())
                    .plusSeconds(transportParentDTO.getT_takeTime().getSecond());
        }

        if(transportParentDTOS.size() == 2){
            loseTime = loseTime.plusSeconds(1);
        }

        //지정된 장소 이후의 장소 정보 가져오기
        List<PlanPlace> planPlaces = planPlaceRepository.findAllAfterId(planNo, ppOrd);

        List<PlanPlaceDTO> planPlaceDTOS = planPlaces.stream()
                .map(planPlace -> modelMapper.map(planPlace, PlanPlaceDTO.class)).collect(Collectors.toList());

        //순환 참조 가능한 리스트로 전환
        ListIterator<PlanPlaceDTO> PlanPlaceIterator = planPlaceDTOS.listIterator();

        while (PlanPlaceIterator.hasNext()) {
            PlanPlaceDTO planPlaceDTO = PlanPlaceIterator.next();

            //NightToNight 1, 2 통합
            if(planPlaceDTO.getNightToNight() == 1){
                if(PlanPlaceIterator.hasNext()){
                    PlanPlaceDTO planPlaceDTO2 = PlanPlaceIterator.next();
                    //부모교통정보는 2번째 ppOrd를 참조하기 때문
                    planPlaceDTO.setPpOrd(planPlaceDTO2.getPpOrd());
                    //NightToNight가 1인 경우 NightToNight = 1이 오늘 보내는 시간 + 내일 보내는 시간 + 1초
                    planPlaceDTO.setPp_takeDate(planPlaceDTO.getPp_takeDate().plusHours(planPlaceDTO2.getPp_takeDate().getHour()).plusMinutes(planPlaceDTO2.getPp_takeDate().getMinute()).plusSeconds(planPlaceDTO2.getPp_takeDate().getSecond()).plusSeconds(1));
                    planPlaceDTO.setNightToNight((byte)0);
                    //NightToNight가 2인 것을 제거
                    PlanPlaceIterator.remove();
                }
            }

            //모든 planPlace 데이터를 - loseTime 해주기
            planPlaceDTO.setPp_startDate(planPlaceDTO.getPp_startDate()
                    .minusHours(loseTime.getHour())
                    .minusMinutes(loseTime.getMinute())
                    .minusSeconds(loseTime.getSecond()));

            if(PlanPlaceIterator.hasNext()){
                PlanPlaceDTO planPlaceDTON = PlanPlaceIterator.next();
                //이전 장소의 출발 날짜 == 다음 장소의 출발 날짜
                if (planPlaceDTO.getPp_startDate().toLocalDate() == planPlaceDTON.getPp_startDate().toLocalDate()) {
                    PlanPlace planPlaced = modelMapper.map(planPlaceDTO, PlanPlace.class);
                    planPlaceRepository.save(planPlaced);

                    //교통 정보 조회
                    List<TransportParent> TransportParents = transportParentRepository.findByPpord(planPlaceDTON.getPpOrd());

                    List<TransportParentDTO> transportParentDTOList = TransportParents.stream()
                            .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class)).collect(Collectors.toList());

                    //모든 교통 정보 - loseTime
                    for (TransportParentDTO transportParentDTO : transportParentDTOList){
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

                    //장소 더하기
                    //교통정보가 2개일 경우
                    if(transportParentDTOList.size() == 2){
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
                                .NightToNight((byte) 0)
                                .build();
                        //출발 시간 == 도착시간
                        LocalDateTime finalTime = transportParentDTOPrev.getT_startDateTime().withHour(23).withMinute(59)
                                .withSecond(59);
                        if(transportParentDTOPrev.getT_startDateTime().toLocalDate() == transportParentDTOPrev.getT_goalDateTime().toLocalDate()){
                            TransportParent transportParent = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParent);
                        //출발 시간 != 도착시간
                        }else{
                            transportParentDTOPrev.setT_goalDateTime(finalTime);
                            TransportParent transportParentNext1 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext1);

                            transportParentDTOPrev.setT_startDateTime(transportParentDTOList.get(1).getT_startDateTime()
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0));
                            transportParentDTOPrev.setT_goalDateTime(transportParentDTOList.get(1).getT_goalDateTime());
                            TransportParent transportParentNext2 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext2);
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
                                .NightToNight((byte) 0)
                                .build();
                        //출발 시간 == 도착시간
                        LocalDateTime finalTime = transportParentDTOPrev.getT_startDateTime().withHour(23).withMinute(59)
                                .withSecond(59);
                        if (transportParentDTOPrev.getT_startDateTime().toLocalDate() == transportParentDTOPrev.getT_goalDateTime().toLocalDate()) {
                            TransportParent transportParent = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParent);
                            //출발 시간 != 도착시간
                        } else {
                            transportParentDTOPrev.setT_goalDateTime(finalTime);
                            TransportParent transportParentNext1 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext1);

                            transportParentDTOPrev.setT_startDateTime(transportParentDTOList.get(1).getT_startDateTime()
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0));
                            transportParentDTOPrev.setT_goalDateTime(transportParentDTOList.get(1).getT_goalDateTime());
                            TransportParent transportParentNext2 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext2);
                        }
                    }
                } else {

                    LocalTime endTime = LocalTime.of(23, 59, 59);

                    // 23:59:59 - 출발시간
                    LocalTime prevTime = endTime.minusHours(planPlaceDTON.getPp_startDate().getHour())
                            .minusMinutes(planPlaceDTON.getPp_startDate().getMinute())
                            .minusSeconds(planPlaceDTON.getPp_startDate().getSecond());
                    // 머무는 시간 - prevTime - 1초
                    LocalTime nextTime = planPlaceDTO.getPp_takeDate().minusHours(prevTime.getHour())
                            .minusMinutes(prevTime.getMinute()).minusSeconds(prevTime.getSecond()).minusSeconds(1);

                    PlanPlace planplace1 = PlanPlace.builder()
                            .ppOrd(planPlaceDTO.getPpOrd())
                            .pp_title(planPlaceDTO.getPp_title())
                            .pp_startAddress(planPlaceDTO.getPp_startAddress()) // planReposi
                            .pp_startDate(planPlaceDTO.getPp_startDate()) // planReposi
                            .pp_takeDate(prevTime) // storeReposi
                            .pp_mapx(planPlaceDTO.getPp_mapx()) // storeReposi
                            .pp_mapy(planPlaceDTO.getPp_mapy()) // storeReposi
                            .planSet(new PlanSet(planNo)) // planReposi
                            .NightToNight((byte) 1)
                            .build();
                    planPlaceRepository.save(planplace1).getPpOrd();

                    PlanPlace planplace2 = PlanPlace.builder()
                            .ppOrd(planPlaceDTO.getPpOrd())
                            .pp_title(planPlaceDTO.getPp_title())
                            .pp_startAddress(planPlaceDTO.getPp_startAddress()) // planReposi
                            .pp_startDate(planPlaceDTON.getPp_startDate().plusDays(1).withHour(0).withMinute(0).withSecond(0)) // planReposi
                            .pp_takeDate(nextTime) // storeReposi
                            .pp_mapx(planPlaceDTO.getPp_mapx()) // storeReposi
                            .pp_mapy(planPlaceDTO.getPp_mapy()) // storeReposi
                            .planSet(new PlanSet(planNo)) // planReposi
                            .NightToNight((byte) 2)
                            .build();

                    planPlaceRepository.save(planplace2).getPpOrd();
                }
            }
        }
        planPlaceRepository.deleteById(ppOrd);
    }

    @Override
    public void updatePlanPlaceTime(Long planNo, Long ppOrd, LocalTime takeTime) {
        //장소 정보 가져오기 및 변환
        //loseTime = 머무는 시간
        Optional<PlanPlace> planPlace1 = planPlaceRepository.findById(ppOrd);

        PlanPlaceDTO planPlaceDTO1 = modelMapper.map(planPlace1, PlanPlaceDTO.class);


        int loseHour = planPlaceDTO1.getPp_takeDate().getHour() - takeTime.getHour();
        int loseMinute = planPlaceDTO1.getPp_takeDate().getHour() - takeTime.getHour();
        int loseSecond = planPlaceDTO1.getPp_takeDate().getHour() - takeTime.getHour();


        //지정된 장소 이후의 장소 정보 가져오기
        List<PlanPlace> planPlaces = planPlaceRepository.findAllAfterId(planNo, ppOrd);

        List<PlanPlaceDTO> planPlaceDTOS = planPlaces.stream()
                .map(planPlace -> modelMapper.map(planPlace, PlanPlaceDTO.class)).collect(Collectors.toList());

        //순환 참조 가능한 리스트로 전환
        ListIterator<PlanPlaceDTO> PlanPlaceIterator = planPlaceDTOS.listIterator();

        while (PlanPlaceIterator.hasNext()) {
            PlanPlaceDTO planPlaceDTO = PlanPlaceIterator.next();

            //NightToNight 1, 2 통합
            if(planPlaceDTO.getNightToNight() == 1){
                if(PlanPlaceIterator.hasNext()){
                    PlanPlaceDTO planPlaceDTO2 = PlanPlaceIterator.next();
                    //부모교통정보는 2번째 ppOrd를 참조하기 때문
                    planPlaceDTO.setPpOrd(planPlaceDTO2.getPpOrd());
                    //NightToNight가 1인 경우 NightToNight = 1이 오늘 보내는 시간 + 내일 보내는 시간 + 1초
                    planPlaceDTO.setPp_takeDate(planPlaceDTO.getPp_takeDate().plusHours(planPlaceDTO2.getPp_takeDate().getHour()).plusMinutes(planPlaceDTO2.getPp_takeDate().getMinute()).plusSeconds(planPlaceDTO2.getPp_takeDate().getSecond()).plusSeconds(1));
                    planPlaceDTO.setNightToNight((byte)0);
                    //NightToNight가 2인 것을 제거
                    PlanPlaceIterator.remove();
                }
            }

            //모든 planPlace 데이터를 - loseTime 해주기
            planPlaceDTO.setPp_startDate(planPlaceDTO.getPp_startDate()
                    .minusHours(loseHour)
                    .minusMinutes(loseMinute)
                    .minusSeconds(loseSecond));

            if(PlanPlaceIterator.hasNext()){
                PlanPlaceDTO planPlaceDTON = PlanPlaceIterator.next();
                //이전 장소의 출발 날짜 == 다음 장소의 출발 날짜
                if (planPlaceDTO.getPp_startDate().toLocalDate() == planPlaceDTON.getPp_startDate().toLocalDate()) {
                    PlanPlace planPlaced = modelMapper.map(planPlaceDTO, PlanPlace.class);
                    planPlaceRepository.save(planPlaced);

                    //교통 정보 조회
                    List<TransportParent> TransportParents = transportParentRepository.findByPpord(planPlaceDTON.getPpOrd());

                    List<TransportParentDTO> transportParentDTOList = TransportParents.stream()
                            .map(transportParent -> modelMapper.map(transportParent, TransportParentDTO.class)).collect(Collectors.toList());

                    //모든 교통 정보 - loseTime
                    for (TransportParentDTO transportParentDTO : transportParentDTOList){
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
                    }

                    //장소 더하기
                    //교통정보가 2개일 경우
                    if(transportParentDTOList.size() == 2){
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
                                .NightToNight((byte) 0)
                                .build();
                        //출발 시간 == 도착시간
                        LocalDateTime finalTime = transportParentDTOPrev.getT_startDateTime().withHour(23).withMinute(59)
                                .withSecond(59);
                        if(transportParentDTOPrev.getT_startDateTime().toLocalDate() == transportParentDTOPrev.getT_goalDateTime().toLocalDate()){
                            TransportParent transportParent = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParent);
                        //출발 시간 != 도착시간
                        }else{
                            transportParentDTOPrev.setT_goalDateTime(finalTime);
                            TransportParent transportParentNext1 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext1);

                            transportParentDTOPrev.setT_startDateTime(transportParentDTOList.get(1).getT_startDateTime()
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0));
                            transportParentDTOPrev.setT_goalDateTime(transportParentDTOList.get(1).getT_goalDateTime());
                            TransportParent transportParentNext2 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext2);
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
                                .NightToNight((byte) 0)
                                .build();
                        //출발 시간 == 도착시간
                        LocalDateTime finalTime = transportParentDTOPrev.getT_startDateTime().withHour(23).withMinute(59)
                                .withSecond(59);
                        if (transportParentDTOPrev.getT_startDateTime().toLocalDate() == transportParentDTOPrev.getT_goalDateTime().toLocalDate()) {
                            TransportParent transportParent = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParent);
                            //출발 시간 != 도착시간
                        } else {
                            transportParentDTOPrev.setT_goalDateTime(finalTime);
                            TransportParent transportParentNext1 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext1);

                            transportParentDTOPrev.setT_startDateTime(transportParentDTOList.get(1).getT_startDateTime()
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0));
                            transportParentDTOPrev.setT_goalDateTime(transportParentDTOList.get(1).getT_goalDateTime());
                            TransportParent transportParentNext2 = modelMapper.map(transportParentDTOPrev, TransportParent.class);
                            transportParentRepository.save(transportParentNext2);
                        }
                    }
                } else {

                    LocalTime endTime = LocalTime.of(23, 59, 59);

                    // 23:59:59 - 출발시간
                    LocalTime prevTime = endTime.minusHours(planPlaceDTON.getPp_startDate().getHour())
                            .minusMinutes(planPlaceDTON.getPp_startDate().getMinute())
                            .minusSeconds(planPlaceDTON.getPp_startDate().getSecond());
                    // 머무는 시간 - prevTime - 1초
                    LocalTime nextTime = planPlaceDTO.getPp_takeDate().minusHours(prevTime.getHour())
                            .minusMinutes(prevTime.getMinute()).minusSeconds(prevTime.getSecond()).minusSeconds(1);

                    PlanPlace planplace1 = PlanPlace.builder()
                            .ppOrd(planPlaceDTO.getPpOrd())
                            .pp_title(planPlaceDTO.getPp_title())
                            .pp_startAddress(planPlaceDTO.getPp_startAddress()) // planReposi
                            .pp_startDate(planPlaceDTO.getPp_startDate()) // planReposi
                            .pp_takeDate(prevTime) // storeReposi
                            .pp_mapx(planPlaceDTO.getPp_mapx()) // storeReposi
                            .pp_mapy(planPlaceDTO.getPp_mapy()) // storeReposi
                            .planSet(new PlanSet(planNo)) // planReposi
                            .NightToNight((byte) 1)
                            .build();
                    planPlaceRepository.save(planplace1).getPpOrd();

                    PlanPlace planplace2 = PlanPlace.builder()
                            .ppOrd(planPlaceDTO.getPpOrd())
                            .pp_title(planPlaceDTO.getPp_title())
                            .pp_startAddress(planPlaceDTO.getPp_startAddress()) // planReposi
                            .pp_startDate(planPlaceDTON.getPp_startDate().plusDays(1).withHour(0).withMinute(0).withSecond(0)) // planReposi
                            .pp_takeDate(nextTime) // storeReposi
                            .pp_mapx(planPlaceDTO.getPp_mapx()) // storeReposi
                            .pp_mapy(planPlaceDTO.getPp_mapy()) // storeReposi
                            .planSet(new PlanSet(planNo)) // planReposi
                            .NightToNight((byte) 2)
                            .build();

                    planPlaceRepository.save(planplace2).getPpOrd();
                }
            }
        }
    }
}
