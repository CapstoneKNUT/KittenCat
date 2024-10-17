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




















    /*//리스트
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){

        //PageResponseDTO<StoreDTO> responseDTO = storeService.list(pageRequestDTO);

        PageResponseDTO<StoreDTO> responseDTO =
                storeService.list(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);
    }*/

    //조회
    /*@GetMapping("/read")
    public void read(String p_key, PageRequestDTO pageRequestDTO, Model model){

        StoreDTO storeDTO = storeService.readOne(p_key);

        log.info(storeDTO);

        model.addAttribute("dto", storeDTO);

    }*/


    //삭제
//    @PreAuthorize("principal.username == #storeDTO.bookmark")
    /*@PostMapping("/remove")
    public String remove(StoreDTO storeDTO, RedirectAttributes redirectAttributes) {


        String p_address  = storeDTO.getP_address();
        log.info("remove store.. " + p_address);

        storeService.remove(p_address);

//        //게시물이 삭제되었다면 첨부 파일 삭제
//        String p_image = storeDTO.getP_image();
//        if (p_image != null && !p_image.isEmpty()) {
//            log.info("remove image.. " + p_image);
//            try {
//                storeService.removeImage(p_image);
//            } catch (Exception e) {
//                log.error("image not removed: " + e.getMessage(), e);
//                // 예외 발생 시 처리 로직 (예: 오류 메시지 저장, 관리자에게 알림 등)
//            }
//        }

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/store/list";


    }*/

}
