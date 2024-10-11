package org.zerock.b01.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.zerock.b01.controller.PlaceController;
import org.zerock.b01.domain.Place;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.zerock.b01.dto.PlaceSearchDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
public class PlaceRepositoryTests {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    public void testSelect() {
        int pord = 1;

        Optional<Place> result = placeRepository.findById(pord);

        Place place = result.orElseThrow();

        log.info(place);

    }

    @Test
    void testFindAllSortedByPOrd() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("pord").ascending());
        List<Place> places = placeRepository.findAll(pageable).getContent();

        assertNotNull(places);
        // 추가 검증 로직을 여기에 작성합니다.
    }
}










