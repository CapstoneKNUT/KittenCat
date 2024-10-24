package org.zerock.b01.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Store;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.StoreDTO;
import org.zerock.b01.service.StoreService;

@Controller
@RequestMapping("/api/store")
@Log4j2
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /*//찜목록 조회
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<StoreDTO>> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<StoreDTO> responseDTO = storeService.list(pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }*/

    // 유저별 찜 목록 조회
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<StoreDTO>> listByUser(@RequestParam String username, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<StoreDTO> responseDTO = storeService.list(username, pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    //유저별 찜 목록의 상세 페이지
    @GetMapping("/read")
    public ResponseEntity<StoreDTO> readOne(@RequestParam String username, @RequestParam Long sno) {
        log.info("readOne store with sno: " + sno + " by user: " + username);
        StoreDTO storeDTO = storeService.readOne(username, sno);
        log.info(storeDTO);
        return ResponseEntity.ok(storeDTO);
    }

    //사용자의 찜목록 제거
    @DeleteMapping("/remove")
    public ResponseEntity<String> remove(@RequestParam String username, @RequestParam Long sno, RedirectAttributes redirectAttributes) {
        log.info("remove store with sno: " + sno + " by user: " + username);
        storeService.remove(username, sno);
        redirectAttributes.addFlashAttribute("result", "removed");
        return ResponseEntity.ok("Store removed successfully");
    }

    // 유저의 찜목록 중 이름을 검색하여 조회
    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<StoreDTO>> searchBookmarks(@RequestParam String username,@RequestParam String p_name, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<StoreDTO> responseDTO = storeService.list(username, pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }


}
