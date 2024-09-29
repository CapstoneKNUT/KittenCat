package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.PlaceDTO;
import org.zerock.b01.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/place")
@Log4j2
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<PlaceDTO>> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<PlaceDTO> responseDTO = placeService.list(pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/list")
    public ResponseEntity<String> registerPost(PageRequestDTO pageRequestDTO) {
        String p_area = pageRequestDTO.getP_area();
        String p_subArea = pageRequestDTO.getP_subArea();
        String p_category = pageRequestDTO.getP_category();
        String p_keyword = pageRequestDTO.getP_keyword();

        RestTemplate restTemplate = new RestTemplate();

        // Set the headers for the HTTP request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the request body with the JSON structure
        String json = String.format("{ \"command\": \"run_script\", \"script_path\": \"../Python/search.py\", \"options\": {\"p_area\": \"%s\", \"p_subArea\": \"%s\", \"p_category\": \"%s\" , \"p_keyword\": \"%s\"} }", p_area, p_subArea, p_category, p_keyword);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);

        // Send the HTTP request to the Python server
        String url = "http://localhost:5000/search";
        String responseDTO = restTemplate.postForObject(url, entity, String.class);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping({"/read"})
    public ResponseEntity<PlaceDTO> read(@RequestParam Integer p_ord) {
        PlaceDTO placeDTO = placeService.readOne(p_ord);
        log.info(placeDTO);
        return ResponseEntity.ok(placeDTO);
    }

}
