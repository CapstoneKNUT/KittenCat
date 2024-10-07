package org.zerock.b01.service;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.StoreDTO;

public interface StoreService {
    // 추가하기
    String register(StoreDTO storeDTO);

    // 제거하기
    void remove(String p_address);

    // 페이지로 나타내기
    PageResponseDTO<StoreDTO> list(PageRequestDTO pageRequestDTO);

    // 상세페이지로 이동
    StoreDTO readOne(String p_address);
}
