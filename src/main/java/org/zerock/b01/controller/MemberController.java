package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.MemberJoinDTO;
import org.zerock.b01.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public ResponseEntity<Map<String, String>> joinGET() {
        log.info("join get...");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Please provide member details to join.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> joinPOST(@RequestBody MemberJoinDTO memberJoinDTO) {
        log.info("join post...");
        log.info(memberJoinDTO);

        Map<String, String> response = new HashMap<>();

        try {
            memberService.join(memberJoinDTO);
            response.put("result", "success");
            return ResponseEntity.ok(response);
        } catch (MemberService.MidExistException e) {
            response.put("error", "mid");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginGET(@RequestParam(required = false) String error, @RequestParam(required = false) String logout) {
        log.info("login get..............");
        log.info("logout: " + logout);

        Map<String, String> response = new HashMap<>();
        if (error != null) {
            response.put("error", "Login error occurred.");
        }
        if (logout != null) {
            response.put("logout", "You have been logged out.");
        }
        return ResponseEntity.ok(response);
    }
}
