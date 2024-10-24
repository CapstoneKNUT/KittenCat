package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Place;
import org.zerock.b01.domain.Store;
import org.zerock.b01.service.StoreService;

import java.util.Optional;

@SpringBootTest
@Log4j2
public class StoreRepositoryTests {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private StoreService storeService;


   /* //Store 테이블에서 제거하기
    @Test
    public void testRemoveStore() {
        //제거하려는 Store의 sno와 bookmark를 설정
        Long sno = 4L; // 실제 존재하는 sno로 변경
        String username = "user0";
        //remove 메서드 호출하여 St/ore 제거
        storeService.remove(sno,username);
    }*/


    @Test
    public void testSelect() {
        Long sno = 2L;

        Optional<Store> result = storeRepository.findById(sno);

        Store store = result.orElseThrow();

        log.info(store);

    }

    // 회원별 찜 목록 출력 테스트
    @Test
    public void testsearchAll(){
        String username = "예찬";

        Pageable pageable = PageRequest.of(0,10, Sort.by("sno").descending());

//        Page<Store> result = storeRepository.findByMidMid(username, pageable);
        Page<Store> result = storeRepository.findByMid_Mid(username, pageable);

        log.info(result);
    }

}










