package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PlaceDTO;
import org.zerock.b01.dto.PlaceListAllDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.service.PlaceService;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
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
    public ResponseEntity<PageResponseDTO<PlaceListAllDTO>> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<PlaceListAllDTO> responseDTO = placeService.listWithAll(pageRequestDTO);
        log.info(responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasRole('USER')")
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
            Long bno = placeService.register(placeDTO);
            response.put("result", "success");
            response.put("bno", bno);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration error: ", e);
            response.put("error", "Registration failed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/read", "/modify"})
    public ResponseEntity<PlaceDTO> read(@RequestParam Long bno, PageRequestDTO pageRequestDTO) {
        PlaceDTO placeDTO = placeService.readOne(bno);
        log.info(placeDTO);
        return ResponseEntity.ok(placeDTO);
    }

    @PreAuthorize("principal.username == #placeDTO.writer")
    @PostMapping("/modify")
    public ResponseEntity<Map<String, String>> modify(
            @RequestBody @Valid PlaceDTO placeDTO,
            PageRequestDTO pageRequestDTO) {

        log.info("place modify post......." + placeDTO);

        Map<String, String> response = new HashMap<>();
        try {
            placeService.modify(placeDTO);
            response.put("result", "modified");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Modification error: ", e);
            response.put("error", "Modification failed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("principal.username == #placeDTO.writer")
    @PostMapping("/remove")
    public ResponseEntity<Map<String, String>> remove(@RequestBody PlaceDTO placeDTO) {
        Long bno = placeDTO.getBno();
        log.info("remove post.. " + bno);

        Map<String, String> response = new HashMap<>();
        try {
            placeService.remove(bno);
            List<String> fileNames = placeDTO.getFileNames();
            if (fileNames != null && !fileNames.isEmpty()) {
                removeFiles(fileNames);
            }
            response.put("result", "removed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Removal error: ", e);
            response.put("error", "Removal failed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    private void removeFiles(List<String> files) {
        for (String fileName : files) {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();
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
