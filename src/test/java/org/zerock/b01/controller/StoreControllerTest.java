package org.zerock.b01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

    //일반 테스트. 특정 사용자의 찜목록 반환.
    @Test
    void testListByUser() throws Exception {
        // Arrange
        String username = "user0";
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        StoreDTO storeDTO = StoreDTO.builder()
                .sno(1L)
                .p_name("Test CoFFee")
                .p_category("cafe")
                .p_address("123 Test Address")
                .p_content("Water is Self")
                .p_image("..")
                .bookmark(username)
                .p_opentime("Mon~Fri 09:00~21:00")
                .p_park("We don'thave")
                .p_star(4.5f)
                .build();

        PageResponseDTO<StoreDTO> mockResponse = PageResponseDTO.<StoreDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(Collections.singletonList(storeDTO))
                .total(1)
                .build();

        when(storeService.list(eq(username), any(PageRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/store/list")
                        .param("username", username)
                        .param("page", String.valueOf(pageRequestDTO.getPage()))
                        .param("size", String.valueOf(pageRequestDTO.getSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(pageRequestDTO.getPage()))
                .andExpect(jsonPath("$.size").value(pageRequestDTO.getSize()))
                .andExpect(jsonPath("$.total").value(mockResponse.getTotal()))
                .andExpect(jsonPath("$.dtoList").isArray())
                .andExpect(jsonPath("$.dtoList[0].p_name").value("Test CoFFee"))
                .andExpect(jsonPath("$.dtoList[0].p_address").value("123 Test Address"))
                .andExpect(jsonPath("$.dtoList[0].p_star").value(4.5));

        verify(storeService, times(1)).list(eq(username), any(PageRequestDTO.class));
    }

    //찜목록이 빈 사용자의 경우 빈 목록이 반환됨.
    @Test
    void testListByUser_EmptyList() throws Exception {
        // Arrange
        String username = "user1"; // 다른 사용자
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<StoreDTO> mockResponse = PageResponseDTO.<StoreDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(Collections.emptyList()) // 빈 리스트 반환
                .total(0) // 총 아이템 수 0
                .build();

        when(storeService.list(eq(username), any(PageRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/store/list")
                        .param("username", username)
                        .param("page", String.valueOf(pageRequestDTO.getPage()))
                        .param("size", String.valueOf(pageRequestDTO.getSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.dtoList").isEmpty());

        verify(storeService, times(1)).list(eq(username), any(PageRequestDTO.class));
    }

    //상세페이지 테스트
    @Test
    public void testReadStore() throws Exception {
        String username = "user0";
        // 테스트용 가짜 데이터
        StoreDTO mockStoreDTO = StoreDTO.builder()
                .sno(5L)
                .p_name("Test CoFFee")
                .p_category("cafe")
                .p_address("123 Test Address")
                .p_content("Water is Self")
                .p_image("..")
                .bookmark(username)
                .p_opentime("Mon~Fri 09:00~21:00")
                .p_park("We don'thave")
                .p_star(4.5f)
                .build();

        // 목 서비스 동작 설정
        when(storeService.readOne(any(String.class), any(Long.class)))
                .thenReturn(mockStoreDTO);

        // GET 요청에 대한 테스트
        mockMvc.perform(get("/api/store/read")
                        .param("username", "user0")
                        .param("sno", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sno").value(5L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.p_name").value("Test CoFFee"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.p_address").value("123 Test Address"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

/*    //존재하지 않는 사용자
    @Test
    void testListByUser_UserNotFound() throws Exception {
        // Arrange
        String username = "nonexistentUser"; // 존재하지 않는 사용자
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        when(storeService.list(eq(username), any(PageRequestDTO.class))).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/api/store/list")
                        .param("username", username)
                        .param("page", String.valueOf(pageRequestDTO.getPage()))
                        .param("size", String.valueOf(pageRequestDTO.getSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // 예외 처리로 인해 500 에러 발생

        verify(storeService, times(1)).list(eq(username), any(PageRequestDTO.class));
    }*/
}
