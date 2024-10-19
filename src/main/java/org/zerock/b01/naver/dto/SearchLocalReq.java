package org.zerock.b01.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLocalReq {
    // 지역 검색 요청 변수에 대한 변수 생성
    private String query = "";  // 검색을 원하는 문자열로서 UTF-8로 인코딩한다.

    public MultiValueMap<String, String> toMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();

        map.add("query", query);

        return map;

    }
}
