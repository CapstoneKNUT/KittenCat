package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {

    @NotEmpty
    private String p_address; // 여행지 주소

    private String bookmark; // 한 명의 사용자 ID 저장

    @NotEmpty
    private String p_name;

    @NotEmpty
    private String p_category;

    private byte[] p_image;

    private float p_star;
}
