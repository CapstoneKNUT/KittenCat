package org.zerock.b01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.StoreDTO;
import org.zerock.b01.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListByUser() throws Exception {
        // Arrange
        String username = "user0"; // 사용자 이름
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10) // 페이지 크기 설정
                .build();

        StoreDTO storeDTO = StoreDTO.builder()
                .p_name("Test Store")
                .p_address("123 Test Address")
                .p_star(4.5f)
                .build();

        PageResponseDTO<StoreDTO> mockResponse = PageResponseDTO.<StoreDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(Collections.singletonList(storeDTO)) // 단일 아이템 리스트
                .total(1) // 총 아이템 수
                .build();

        // StoreService의 list 메소드가 특정 파라미터로 호출되었을 때 mockResponse 반환
        when(storeService.list(eq(username), any(PageRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/store/list")
                        .param("username", username) // 올바른 파라미터 이름으로 수정
                        .param("page", String.valueOf(pageRequestDTO.getPage()))
                        .param("size", String.valueOf(pageRequestDTO.getSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(pageRequestDTO.getPage()))
                .andExpect(jsonPath("$.size").value(pageRequestDTO.getSize()))
                .andExpect(jsonPath("$.total").value(mockResponse.getTotal()))
                .andExpect(jsonPath("$.dtoList").isArray())
                .andExpect(jsonPath("$.dtoList[0].p_name").value("Test Store"))
                .andExpect(jsonPath("$.dtoList[0].p_address").value("123 Test Address"))
                .andExpect(jsonPath("$.dtoList[0].p_star").value(4.5));

        // StoreService의 list 메소드가 정확히 한 번 호출되었는지 검증
        verify(storeService, times(1)).list(eq(username), any(PageRequestDTO.class));
    }
}
