package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Place;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class PlaceRepositoryTests {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            Place place = Place.builder()
                    .p_name("PlaceName"+ i)
                    .p_category("Category"+ i)
                    .p_address("Address"+ i)
                    .p_content("Content"+ i)
                    .bookmark("Bookmark"+ i)
                    .p_image("Image"+ i)
                    .p_call("Call"+ i)
                    .p_star((float)i)
                    .p_site("site"+ i)
                    .build();

            Place result = placeRepository.save(place);
            log.info("BNO: " + result.getP_ord());
        });
    }

    @Test
    public void testSelect() {
        Integer p_ord = 100;

        Optional<Place> result = placeRepository.findById(p_ord);

        Place place = result.orElseThrow();

        log.info(place);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {

        Integer p_ord =61;
        placeRepository.deleteById(p_ord);
    }

}










