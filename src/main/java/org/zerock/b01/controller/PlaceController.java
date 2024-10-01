package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.PlaceDTO;
import org.zerock.b01.dto.PlaceSearchDTO;
import org.zerock.b01.service.PlaceService;

import java.io.IOException;

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
    public ResponseEntity<String> registerPost(@RequestBody PlaceSearchDTO placeSearchDTO) {
        String p_area = placeSearchDTO.getP_area();
        String p_subArea = placeSearchDTO.getP_subArea();
        String p_category = placeSearchDTO.getP_category();
        Integer p_count = placeSearchDTO.getP_count();
        String p_keyword = placeSearchDTO.getP_keyword();

        try{
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python3", "../python/place.py", p_area, p_subArea, p_category, String.valueOf(p_count), p_keyword
            );
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if(exitCode == 0){
                return ResponseEntity.ok("스크립트가 성공적으로 실행 완료 됨");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파이썬 실행 중 오류 발생 : " + exitCode);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파이썬 실행 중 예외 발생 : " + e.getMessage());
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파이썬 실행 중 예외 발생: " + e.getMessage());
        }
    }

    @GetMapping({"/read"})
    public ResponseEntity<PlaceDTO> read(@RequestParam Integer p_ord) {
        PlaceDTO placeDTO = placeService.readOne(p_ord);
        log.info(placeDTO);
        return ResponseEntity.ok(placeDTO);
    }

}
