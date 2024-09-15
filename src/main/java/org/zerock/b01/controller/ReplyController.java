package org.zerock.b01.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @ApiOperation(value = "Replies POST", notes = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> register(
            @Valid @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult) throws BindException {

        log.info(replyDTO);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Long rno = replyService.register(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultMap);
    }

    @ApiOperation(value = "Replies of Board", notes = "GET 방식으로 특정 게시물의 댓글 목록")
    @GetMapping(value = "/list/{bno}")
    public ResponseEntity<PageResponseDTO<ReplyDTO>> getList(
            @PathVariable("bno") Long bno,
            PageRequestDTO pageRequestDTO,
            @RequestParam(name = "type", required = false, defaultValue = "board") String type) {

        PageResponseDTO<ReplyDTO> responseDTO;

        switch (type.toLowerCase()) {
            case "place":
                responseDTO = replyService.getListOfPlace(bno, pageRequestDTO);
                break;
            case "board":
            default:
                responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);
                break;
        }

        return ResponseEntity.ok(responseDTO);
    }

    @ApiOperation(value = "Read Reply", notes = "GET 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ResponseEntity<ReplyDTO> getReplyDTO(@PathVariable("rno") Long rno) {
        ReplyDTO replyDTO = replyService.read(rno);
        return ResponseEntity.ok(replyDTO);
    }

    @ApiOperation(value = "Delete Reply", notes = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public ResponseEntity<Map<String, Long>> remove(@PathVariable("rno") Long rno) {
        try {
            replyService.remove(rno);

            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("rno", rno);

            return ResponseEntity.ok(resultMap);
        } catch (NoSuchElementException e) {
            log.error("No reply found with rno: " + rno, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @ApiOperation(value = "Modify Reply", notes = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> modify(
            @PathVariable("rno") Long rno,
            @RequestBody @Valid ReplyDTO replyDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Long> errorMap = new HashMap<>();
            errorMap.put("error", -1L);
            return ResponseEntity.badRequest().body(errorMap);
        }

        replyDTO.setRno(rno); // 번호를 일치시킴
        replyService.modify(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);

        return ResponseEntity.ok(resultMap);
    }
}
