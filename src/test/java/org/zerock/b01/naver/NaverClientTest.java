package org.zerock.b01.naver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    }
}
