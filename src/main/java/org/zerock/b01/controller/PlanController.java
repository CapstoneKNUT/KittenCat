package org.zerock.b01.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.PlaceDTO;
import org.zerock.b01.dto.PlaceSearchDTO;
import org.zerock.b01.service.ApiService;
import org.zerock.b01.service.PlaceService;

import java.util.Map;

@RestController
@RequestMapping("/api/plan")
@Log4j2
@RequiredArgsConstructor
public class PlanController {

    @GetMapping("/list")
    public ResponseEntity<?> list(){

    }

    @PostMapping("/list")
    public ResponseEntity<?> searchPost(@RequestBody  PlaceSearchDTO placeSearchDTO) {

    }

    @GetMapping({ "/read" })
    public ResponseEntity<PlaceDTO> read(@RequestParam Integer pord) {

    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@RequestBody Integer pord, String username) {

    }
}