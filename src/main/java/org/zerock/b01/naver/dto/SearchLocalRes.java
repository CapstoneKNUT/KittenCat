package org.zerock.b01.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLocalRes {
    private List<Address> addresses;

    @Data
    public static class Address {
        private String roadAddress;
        private String jibunAddress;
        private String x; // 경도
        private String y; // 위도
    }
}
