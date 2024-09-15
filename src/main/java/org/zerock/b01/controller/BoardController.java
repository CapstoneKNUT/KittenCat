package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.*;
import org.zerock.b01.service.BoardService;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<BoardListAllDTO>> list(PageRequestDTO pageRequestDTO) {

        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        log.info(responseDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/register")
    public ResponseEntity<Void> registerGET() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> registerPost(@RequestBody @Valid BoardDTO boardDTO) {

        log.info("board POST register.......");

        Long bno = boardService.register(boardDTO);

        return ResponseEntity.ok(Map.of("bno", bno));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/read", "/modify"})
    public ResponseEntity<BoardDTO> read(@RequestParam Long bno, PageRequestDTO pageRequestDTO) {

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        return ResponseEntity.ok(boardDTO);
    }

    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping(value = "/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> modify(
            @RequestBody @Valid BoardDTO boardDTO,
            PageRequestDTO pageRequestDTO) {

        log.info("board modify post......." + boardDTO);

        boardService.modify(boardDTO);

        return ResponseEntity.ok(Map.of("result", "modified"));
    }

    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> remove(@RequestBody BoardDTO boardDTO) {

        Long bno = boardDTO.getBno();
        log.info("remove post.. " + bno);

        boardService.remove(bno);

        // 게시물이 삭제되었다면 첨부 파일 삭제
        log.info(boardDTO.getFileNames());
        List<String> fileNames = boardDTO.getFileNames();
        if (fileNames != null && !fileNames.isEmpty()) {
            removeFiles(fileNames);
        }

        return ResponseEntity.ok(Map.of("result", "removed"));
    }

    private void removeFiles(List<String> files) {

        for (String fileName : files) {

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();

            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                // 섬네일이 존재한다면
                if (contentType != null && contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
