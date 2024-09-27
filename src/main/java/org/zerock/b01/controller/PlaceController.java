package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zerock.b01.dto.PlaceDTO;
import org.zerock.b01.dto.PlaceListAllDTO;
import org.zerock.b01.service.PlaceService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/place")
@Log4j2
@RequiredArgsConstructor
public class PlaceController {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    private final PlaceService placeService;


    @GetMapping("/list")
    public ResponseEntity<List<PlaceListAllDTO>> list() {
        List<PlaceListAllDTO> responseDTO = placeService.list();
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/list")
    public ResponseEntity<String> registerPost(PageRequestDTO pageRequestDTO) {
        String p_region = pageRequestDTO.getP_region();
        String p_category = pageRequestDTO.getP_category();
        String p_search = pageRequestDTO.getP_search();

        RestTemplate restTemplate = new RestTemplate();

        // Set the headers for the HTTP request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set the request body with the JSON structure
        String json = String.format("{ \"command\": \"run_script\", \"script_path\": \"../Python/search.py\", \"options\": {\"p_region\": \"%s\", \"p_category\": \"%s\" , \"p_search\": \"%s\"} }", p_region, p_category, p_search);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);

        // Send the HTTP request to the Python server
        String url = "http://localhost:5000/search";
        String responseDTO = restTemplate.postForObject(url, entity, String.class);

        return ResponseEntity.ok(responseDTO);;
    }

    @GetMapping("/register")
    public ResponseEntity<Map<String, String>> registerGET() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Please provide place details to register.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerPost(@RequestBody @Valid PlaceDTO placeDTO) {
        log.info("place POST register.......");
        log.info(placeDTO);

        Map<String, Object> response = new HashMap<>();
        try {
            Integer bno = placeService.register(placeDTO);
            response.put("result", "success");
            response.put("bno", bno);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration error: ", e);
            response.put("error", "Registration failed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping({"/read"})
    public ResponseEntity<PlaceDTO> read(@RequestParam Integer p_ord) {
        PlaceDTO placeDTO = placeService.readOne(p_ord);
        log.info(placeDTO);
        return ResponseEntity.ok(placeDTO);
    }

}
