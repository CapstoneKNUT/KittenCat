package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.zerock.b01.domain.PlanPlace;
import org.zerock.b01.domain.PlanSet;
import org.zerock.b01.dto.PlanPlaceDTO;
import org.zerock.b01.dto.PlanSetDTO;
import org.zerock.b01.dto.Search.getXYRequest;
import org.zerock.b01.dto.Search.getXYResponse;
import org.zerock.b01.repository.PlanPlaceRepository;
import org.zerock.b01.repository.PlanRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    private final PlanPlaceRepository planPlaceRepository;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverSecret;

    @Value("${naver.url.search.local}")
    private String naverLocalSearchUrl;

    @Override
    public Long registerInit(PlanSetDTO planSetDTO) {

        PlanSet planSet = dtoToEntity(planSetDTO);

        Long planNo = planRepository.save(planSet).getPlanNo();

        return planNo;
    }

    @Override
    public Map<String, Integer> getXY(getXYRequest getXYRequest) {
        var uri = UriComponentsBuilder
                .fromUriString(naverLocalSearchUrl)
                .queryParams(getXYRequest.toMultiValueMap())
                .build()
                .encode()
                .toUri();

        var headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverClientId);
        headers.set("X-Naver-Client-Secret", naverSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var httpEntity = new HttpEntity<>(headers);
        var responseType = new ParameterizedTypeReference<getXYResponse>() {
        };

        var responseEntity = new RestTemplate()
                .exchange(
                        uri,
                        HttpMethod.GET,
                        httpEntity,
                        responseType);

        getXYResponse response = responseEntity.getBody();

        List<getXYResponse.SearchLocalItem> items = response.getItems();

        if (!items.isEmpty()) {
            getXYResponse.SearchLocalItem item = items.get(0);
            int mapx = item.getMapx();
            int mapy = item.getMapy();

            log.info("mapx: {}, mapy: {}", mapx, mapy);
            return Map.of("mapx", mapx, "mapy", mapy);
        }
        return Map.of();
    }

    @Override
    public Long registerPP(PlanPlaceDTO planPlaceDTO) {

        PlanPlace planplace = dtoToEntityPP(planPlaceDTO);

        Long ppOrd = planPlaceRepository.save(planplace).getPpOrd();

        return ppOrd;
    }
}
