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
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.Search.DrivingRequest;
import org.zerock.b01.dto.Search.DrivingResponse;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;
import org.zerock.b01.repository.PlanPlaceRepository;
import org.zerock.b01.repository.PlanRepository;
import org.zerock.b01.repository.TransportChildRepository;
import org.zerock.b01.repository.TransportParentRepository;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
    public PlanPlaceDTO readPlanPlace(Long ppOrd) {

        Optional<PlanPlace> result = planPlaceRepository.findById(ppOrd);

        PlanPlace planplace = result.orElseThrow();

        PlanPlaceDTO planplaceDTO = entityToDTOPP(planplace);

        return planplaceDTO;
    }

    @Override
    public TransportParentDTO readTransportParent(Long ppOrd) {

        Optional<TransportParent> result = Optional.ofNullable(transportParentRepository.findByPpord(ppOrd));

        TransportParent transportParent = result.orElseThrow();

        TransportParentDTO transportParentDTO = entityToDTOTP(transportParent);

        return transportParentDTO;
    }

    @Override
    public TransportChildDTO readTransportChild(Long tno) {

        List<TransportChild> result = transportChildRepository.findByTno(tno);

        if (result.isEmpty()) {
            return null;  // 또는 예외 처리 로직 추가
        }

        TransportChild transportChild = result.get(0);

        TransportChildDTO transportChildDTO = entityToDTOTP(transportChild);

        return transportChildDTO;
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

        //DTO로 변환해서 꺼내쓰기
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
        if(LastPlanPlace.getPp_startAddress() != Address){
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

                    //출발알 = 도착일 같을 시 하나만 생성
                    if (LastPlanPlaceDTO.getPp_startDate().toLocalDate() == pp_startDate.toLocalDate()){
                        TransportParent transportParent = TransportParent.builder()
                                .isCar(true)
                                .t_method("차")
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate())
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(pp_startDate)
                                .writer(writer)
                                .NightToNight(false)
                                .build();

                        transportParentRepository.save(transportParent);
                        getTNumber = 1;
                    }else {
                        //출발 시간 = 도착 시간 다를 시 두개 생성
                        LocalDateTime finalTime = LastPlanPlaceDTO.getPp_startDate().withHour(23).withMinute(59).withSecond(59);
                        TransportParent transportParent1 = TransportParent.builder()
                                .isCar(true)
                                .t_method("차")
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate())
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(finalTime)
                                .writer(writer)
                                .NightToNight(true)
                                .build();

                        transportParentRepository.save(transportParent1);

                        TransportParent transportParent2 = TransportParent.builder()
                                .isCar(true)
                                .t_method("차")
                                .t_startDateTime(LastPlanPlaceDTO.getPp_startDate().plusDays(1).withHour(0).withMinute(0).withSecond(0))
                                .t_takeTime(pp_takeTime)
                                .t_goalDateTime(pp_startDate)
                                .writer(writer)
                                .NightToNight(true)
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

                    String result = apiService.callTransportApi("http://localhost:8000/plan/transport/add", transTimeDTO);
                    //최신 교통수단 저장내용 조회
                    TransportParent LastTransportParent = transportParentRepository.findLastTransportParent(writer);
                    //출발시간 지정
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
            } else{
                TransTimeDTO transTimeDTO = TransTimeDTO.builder()
                        .writer(writer)
                        .t_startHour(pp_startDate.getHour())
                        .t_startMinute(pp_startDate.getMinute())
                        .start_location(LastPlanPlace.getPp_startAddress())
                        .arrive_location(Address)
                        .isCar(false)
                        .build();

                String result = apiService.callTransportApi("http://localhost:8000/plan/transport/add", transTimeDTO);
                //최신 저장내용 조회
                TransportParent LastTransportParent = transportParentRepository.findLastTransportParent(writer);
                //출발시간 지정
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
        }else{
            Map<String, Object> result = new HashMap<>();
            //마지막 저장 장소 시작 일시 + 머무는 시간
            result.put("pp_startDate", LastPlanPlaceDTO.getPp_startDate().plusHours(LastPlanPlaceDTO.getPp_takeDate().getHour()).plusMinutes(LastPlanPlaceDTO.getPp_takeDate().getMinute()));
            result.put("getTNumber", 0);
            return result; // 반환
        }
    }
    // 시작 장소 조회

    @Override
    public void removePlanSet(Long planNo){
        planRepository.deleteById(planNo);
    }

    @Override
    public void removePlanPlace(Long ppOrd){
        planPlaceRepository.deleteById(ppOrd);
    }
}
