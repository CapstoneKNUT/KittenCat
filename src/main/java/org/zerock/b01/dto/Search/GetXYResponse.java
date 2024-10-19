package org.zerock.b01.dto.Search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetXYResponse {
    private List<GetXYResponse.Address> addresses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String x; // 경도
        private String y; // 위도
    }
}
