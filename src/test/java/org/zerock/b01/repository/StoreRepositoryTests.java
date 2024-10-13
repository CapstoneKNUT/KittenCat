package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class StoreRepositoryTests {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PlaceRepository placeRepository;

}










