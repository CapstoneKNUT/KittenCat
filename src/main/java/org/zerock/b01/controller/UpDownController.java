package org.zerock.b01.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.b01.dto.upload.UploadFileDTO;
import org.zerock.b01.dto.upload.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api")
@Log4j2
public class UpDownController {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @ApiOperation(value = "Upload POST", notes = "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(@ModelAttribute UploadFileDTO uploadFileDTO) {

        log.info(uploadFileDTO);

        List<UploadResultDTO> result = new ArrayList<>();

        if (uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach(multipartFile -> {

                String originalName = multipartFile.getOriginalFilename();
                log.info("Uploading file: " + originalName);

                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);
                boolean isImage = false;

                try {
                    multipartFile.transferTo(savePath);

                    // 이미지 파일의 경우 썸네일 생성
                    if (Files.probeContentType(savePath).startsWith("image")) {
                        isImage = true;
                        File thumbnailFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 200, 200);
                    }

                } catch (IOException e) {
                    log.error("Error while uploading file", e);
                }

                result.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(isImage)
                        .build());
            });
        }

        return result;
    }

    @ApiOperation(value = "View file", notes = "GET 방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(Paths.get(uploadPath, fileName).toString());
        String contentType;
        HttpHeaders headers = new HttpHeaders();

        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        } catch (IOException e) {
            log.error("Error while getting file content type", e);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @ApiOperation(value = "Remove file", notes = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {

        File file = new File(uploadPath, fileName);
        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            String contentType = Files.probeContentType(file.toPath());
            removed = file.delete();

            // 이미지 파일의 경우 썸네일도 삭제
            if (contentType != null && contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath, "s_" + fileName);
                thumbnailFile.delete();
            }

        } catch (IOException e) {
            log.error("Error while deleting file", e);
        }

        resultMap.put("result", removed);

        return resultMap;
    }
}
