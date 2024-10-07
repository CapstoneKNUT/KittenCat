package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreListAllDTO {

    private String p_key;

    private String p_name;

    private String p_address;

    private String p_category;

    private String p_image;

    private float p_star;
}

//PageResponseDTO<StoreDTO> list(PageRequestDTO pageRequestDTO); 형태를 사용하는 것이 더 효율적이고 명확하다고함