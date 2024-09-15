package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MBTIRequestDTO {
    private String type; // 검색의 종류 t,c, w, tc,tw, twc

    public String getTypes(){
        if(type == null || type.isEmpty()){
            return null;
        }
        return type;
    }
}
