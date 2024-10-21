package org.zerock.b01.dto.Search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrivingRequest {
    private String start; // 출발지 좌표 (경도,위도 형식)
    private String goal; // 도착지 좌표 (경도,위도 형식)
}
