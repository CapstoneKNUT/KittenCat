package org.zerock.b01.service;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.StoreDTO;

public interface StoreService {

    // 페이지로 나타내기
    PageResponseDTO<StoreDTO> list(String username, PageRequestDTO pageRequestDTO);


    // 상세페이지로 이동
    StoreDTO readOne(String username, Long sno);

    StoreDTO read(Long sno);

    // 제거하기
    void remove(String username, Long sno);

    // 여행지 이름 검색하여 찜목록 조회
    PageResponseDTO<StoreDTO> searchBookmarks(String username, String p_name, String p_address, PageRequestDTO pageRequestDTO);




    // (테스트용) 추가하기
//    String register(StoreDTO storeDTO);
}
