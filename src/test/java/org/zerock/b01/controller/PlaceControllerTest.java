package org.zerock.b01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.zerock.b01.dto.PlaceSearchDTO;

import javax.servlet.AsyncContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        MvcResult mvcResult = mockMvc.perform(post("/api/place/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isFound())
                .andReturn();

        // Assert: 리다이렉트 경로 확인
        String location = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        assertNotNull(location);  // 리다이렉트 URL이 null이 아님을 확인
        assertTrue(location.contains("http://localhost:3000/place/list"));  // 실제 응답으로 받은 URL에 대한 검증
    }

    @Test
    void read() {
    }
}