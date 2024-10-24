package org.zerock.b01.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.dto.PlanPlaceDTO;
import org.zerock.b01.dto.PlanSetDTO;
import org.zerock.b01.dto.Search.DrivingRequest;
import org.zerock.b01.dto.Search.DrivingResponse;
import org.zerock.b01.dto.Search.GetXYRequest;
import org.zerock.b01.dto.Search.GetXYResponse;
import org.zerock.b01.dto.TransTimeDTO;
import org.zerock.b01.repository.PlanPlaceRepository;
import org.zerock.b01.repository.PlanRepository;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    private final PlanPlaceRepository planPlaceRepository;

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
    public PlanPlaceDTO readOne(Long ppOrd) {

        Optional<PlanPlace> result = planPlaceRepository.findById(ppOrd);

        PlanPlace planplace = result.orElseThrow();

        PlanPlaceDTO planplaceDTO = entityToDTOPP(planplace);

        return planplaceDTO;
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
    public Long registerPP(PlanPlaceDTO planPlaceDTO) {

        PlanPlace planplace = dtoToEntityPP(planPlaceDTO);

        Long ppOrd = planPlaceRepository.save(planplace).getPpOrd();

        return ppOrd;
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
    public LocalDateTime startTime(Long planNo, String Address, float mapx, float mapy) {
        // 자차 여부 조회
        PlanSetDTO planSetDTO = InitReadOne(planNo);
        Boolean isCar = planSetDTO.getIsCar();

        // 마지막 저장 장소 조회
        PlanPlace LastPlanPlace = planPlaceRepository.findLastPlanPlaceByPlanNo(planNo);

        PlanPlaceDTO LastPlanPlaceDTO = PlanPlaceDTO.builder()
                .pp_startAddress(LastPlanPlace.getPp_startAddress())
                .pp_takeDate(LastPlanPlace.getPp_takeDate())
                .pp_mapx(LastPlanPlace.getPp_mapx())
                .pp_mapy(LastPlanPlace.getPp_mapy())
                .planNo(LastPlanPlace.getPlanSet())
                .build();

        String pp_startAddress = LastPlanPlaceDTO.getPp_startAddress();
        float pp_mapx = LastPlanPlaceDTO.getPp_mapx();
        float pp_mapy = LastPlanPlaceDTO.getPp_mapy();

        // 출발 날짜, 시간
        LocalDateTime pp_startDate = LastPlanPlaceDTO.getPp_startDate();
        // 노는 시간
        LocalTime pp_takeDate = LastPlanPlaceDTO.getPp_takeDate();

        // 출발 시간 + 노는시간
        pp_startDate = pp_startDate.plusHours(pp_takeDate.getHour())
                .plusMinutes(pp_takeDate.getMinute());

        // 출발 시, 출발 분
        int t_startHour = pp_startDate.getHour();
        int t_startMinute = pp_startDate.getMinute();

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
                return pp_startDate;
            } catch (Exception e) {
                TransTimeDTO transTimeDTO = TransTimeDTO.builder()
                        .t_startHour(pp_startDate.getHour())
                        .t_startMinute(pp_startDate.getMinute())
                        .start_location(LastPlanPlace.getPp_startAddress())
                        .arrive_location(Address)
                        .isCar(false)
                        .build();

                String result = apiService.callTransportApi("http://localhost:8000/plan/transport/add", transTimeDTO);

                // Python 스크립트의 응답을 파싱합니다.
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = mapper.readTree(result);
                    int hours = jsonNode.get("hours").asInt();
                    int minutes = jsonNode.get("minutes").asInt();

                    // 계산된 시간을 pp_startDate에 추가합니다.
                    return pp_startDate.plusHours(hours).plusMinutes(minutes);
                } catch (Exception ex) {
                    log.error("JSON 응답 파싱 오류", ex);
                    throw new RuntimeException("이동 시간 계산 실패", ex);
                }
            }
        }

        return pp_startDate;
    }
}
