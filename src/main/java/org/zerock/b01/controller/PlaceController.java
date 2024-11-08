package org.zerock.b01.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.Place;
import org.zerock.b01.dto.*;
import org.zerock.b01.service.ApiService;
import org.zerock.b01.service.PlaceService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/place")
@Log4j2
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    private final ApiService apiService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<PlaceDTO>> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<PlaceDTO> responseDTO = placeService.list(pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/list")
    public ResponseEntity<?> searchPost(@RequestBody PlaceSearchDTO placeSearchDTO) {
        String result = apiService.callExternalApi("http://localhost:8000/place/list", placeSearchDTO);

        // Python 스크립트의 응답을 파싱합니다.
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(result);
            String redirectUrl = jsonNode.get("redirect_url").asText();

            // 리다이렉트 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        } catch (Exception e) {
            log.error("Error parsing JSON response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the request");
        }
    }

    @GetMapping({ "/read" })
    public ResponseEntity<PlaceDTO> read(@RequestParam Integer pord) {
        PlaceDTO placeDTO = placeService.readOne(pord);
        log.info(placeDTO);
        return ResponseEntity.ok(placeDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@RequestBody BookmarkDTO bookmarkDTO) {
        log.info("Received request to register bookmark: {}", bookmarkDTO); // 로그 추가
        String mid = bookmarkDTO.getUsername();
        Long sno = placeService.register(bookmarkDTO.getPord(), mid);
        log.info("Registered place with sno: {}", sno);
        return ResponseEntity.ok(Map.of("sno", sno));
    }

    /*@PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@RequestParam Integer pord, @RequestParam String username) {
        log.info("Received request to register bookmark with pord: {}, username: {}", pord, username);
        Long sno = placeService.register(pord, username);
        log.info("Registered place with sno: {}", sno);
        return ResponseEntity.ok(Map.of("sno", sno));
    }*/


}
