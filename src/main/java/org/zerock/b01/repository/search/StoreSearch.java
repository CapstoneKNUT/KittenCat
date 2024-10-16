package org.zerock.b01.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.Store;

public interface StoreSearch {
//    원본 보호용 주석처리
    Page<Store> searchAll(String username, Pageable pageable);

//    유저별 찜목록 조회를 위한 bookmark 매개변수 추가
//    Page<Store> searchAll(String bookmark, String keyword, Pageable pageable);
}
