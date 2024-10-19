package org.zerock.b01.naver;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.zerock.b01.naver.dto.DrivingRequest;
import org.zerock.b01.naver.dto.DrivingResponse;
import org.zerock.b01.naver.dto.SearchLocalReq;
import org.zerock.b01.naver.dto.SearchLocalRes;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NaverClientTest {
    @Autowired
    private NaverClient naverClient;

    @Test
    void localSearch() {
        var search = new SearchLocalReq();
        search.setQuery("서울 강남구 강남대로96길 22 2층");

        var result = naverClient.localSearch(search);
        System.out.println(result);

        assertNotNull(result);
        assertFalse(result.getAddresses().isEmpty());

        SearchLocalRes.Address firstAddress = result.getAddresses().get(0);
        assertNotNull(firstAddress.getX());
        assertNotNull(firstAddress.getY());

        System.out.println("도로명 주소: " + firstAddress.getRoadAddress());
        System.out.println("지번 주소: " + firstAddress.getJibunAddress());
        System.out.println("경도(X): " + firstAddress.getX());
        System.out.println("위도(Y): " + firstAddress.getY());

        try {
            float longitude = Float.parseFloat(firstAddress.getX());
            float latitude = Float.parseFloat(firstAddress.getY());
            System.out.println("경도(X) float: " + longitude);
            System.out.println("위도(Y) float: " + latitude);
        } catch (NumberFormatException e) {
            System.out.println("경도 또는 위도 값을 float로 변환하는 데 실패했습니다: " + e.getMessage());
        }
    }

    @Test
    void drivingSearch() {
        // 테스트용 DrivingRequest 객체 생성
        var request = new DrivingRequest();
        request.setStart("126.844856,37.5407361");
        request.setGoal("126.8980711,37.5763214");

        // NaverClient의 Driving 메서드 호출
        var result = naverClient.Driving(request);

        // 결과 검증
        assertNotNull(result);
        assertNotNull(result.getRoute());
        assertNotNull(result.getRoute().getTraoptimal());
        assertTrue(result.getRoute().getTraoptimal().length > 0);
        assertNotNull(result.getRoute().getTraoptimal()[0].getSummary());
        assertNotNull(result.getRoute().getTraoptimal()[0].getSummary().getDuration());

        // 결과 출력
        Integer duration = result.getRoute().getTraoptimal()[0].getSummary().getDuration();
        System.out.println("걸린 시간(초): " + (duration % 60));

        // 선택적: 초를 분으로 변환
        double durationInMinutes = ((duration % 3600) / 60);
        System.out.println("걸린 시간(분): " + String.format("%.2f", durationInMinutes));

        int days = (int) (duration / 86400);  // 1일 = 86400초
        int hours = (int) ((duration % 86400) / 3600);  // 1시간 = 3600초
        int minutes = (int) ((duration % 3600) / 60);  // 1분 = 60초
        int seconds = (int) (duration % 60);  // 나머지 초
    }

    @Test
    void ex() {
        try {
            WebClient client = WebClient.create();

            JsonNode responseJson = client.get().uri(uriBuilder -> uriBuilder.scheme("https")
                    .host("naveropenapi.apigw.ntruss.com")
                    .path("/map-direction-15/v1/driving")
                    .queryParam("start", "126.844856,37.5407361")
                    .queryParam("goal", "126.8980711,37.5763214")
                    .build())
                    .header("X-NCP-APIGW-API-KEY-ID", "g0vlo2rtvd")
                    .header("X-NCP-APIGW-API-KEY", "9CwYh99CvGdHrDqnCthu5TF1WxBqd0vlbGEpjMLl")
                    .retrieve()
                    .bodyToMono(JsonNode.class) // JSON 객체로 파싱
                    .block();

            System.out.println("Response JSON: " + responseJson.toString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
