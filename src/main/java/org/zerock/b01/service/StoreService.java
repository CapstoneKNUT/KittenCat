package org.zerock.b01.service;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.StoreDTO;

public interface StoreService {

    // 페이지로 나타내기
    PageResponseDTO<StoreDTO> list(String username, PageRequestDTO pageRequestDTO);


    // 상세페이지로 이동
    StoreDTO readOne(Long sno);

    // 제거하기
    void remove(Long sno);



    // (테스트용) 추가하기
//    String register(StoreDTO storeDTO);
}
