package org.zerock.b01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.zerock.b01.dto.PlaceSearchDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
class PlaceControllerTest {

    @Autowired
    private PlaceController placeController;

    PlaceSearchDTO placeSearchDTO;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void list() {

    }

    @Test
    void searchPost() throws Exception{
        // PlaceSearchDTO 객체를 생성
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(new PlaceSearchDTO("서울", "강남구", "음식점", 10, "맛집"));

        mockMvc.perform(post("/api/place/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("스크립트가 성공적으로 실행 완료 됨"));    }

    @Test
    void read() {
    }
}