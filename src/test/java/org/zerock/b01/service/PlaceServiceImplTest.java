package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.PlaceDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class PlaceServiceImplTest {

    @Autowired
    private PlaceService placeService;

    @Test
    void readOne() {
        int pord = 1;

        PlaceDTO placeDTO = placeService.readOne(pord);

        log.info(placeDTO);    }

    @Test
    void testList() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        // Given
        pageRequestDTO.setPage(1);
        pageRequestDTO.setSize(10);

        // When
        PageResponseDTO<PlaceDTO> responseDTO = placeService.list(pageRequestDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals(10, responseDTO.getTotal()); // 총 개수가 2인지 확인
        List<PlaceDTO> dtoList = responseDTO.getDtoList();
        assertEquals(10, dtoList.size()); // 리스트의 크기가 2인지 확인
    }


    //place의 pord째 데이터 mid값과 함께 store에 저장
    @Test
    void register(){
        Long result = placeService.register(5,"sun1");
        assertNotNull(result);
    }

}