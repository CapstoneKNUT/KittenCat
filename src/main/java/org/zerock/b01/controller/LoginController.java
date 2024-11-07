package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.MemberDTO;
import org.zerock.b01.service.MemberService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@Log4j2
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO memberDTO, HttpSession session) {
        log.info("Login attempt for user: " + memberDTO.getMid());

        try {
            MemberDTO loggedInUser = memberService.login(memberDTO); // 로그인 서비스 호출
            session.setAttribute("user", loggedInUser); // 세션에 사용자 정보 저장
            return ResponseEntity.ok(loggedInUser); // 로그인된 사용자 정보 응답
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage())); // 로그인 실패 시 에러 메시지 반환
        }
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> join(@RequestBody MemberDTO memberDTO) {
        log.info("Join request...");
        log.info(memberDTO);

        Map<String, String> response = new HashMap<>();

        try {
            memberService.register(memberDTO);
            response.put("result", "success");
            return ResponseEntity.ok(response);
        } catch (MemberService.MidExistException e) {
            response.put("error", "mid");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        String userId = (String) session.getAttribute("user");
        if (userId != null) {
            session.invalidate(); // 세션 무효화
            log.info("User logged out: " + userId);
            return ResponseEntity.ok(Map.of("result", "success", "message", "Logged out successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No user logged in")); // 로그인된 사용자가 없을 경우
    }

    // 회원 정보 수정
    @PutMapping("/modify")
    public ResponseEntity<Map<String, String>> modify(@RequestBody MemberDTO memberDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            memberService.modify(memberDTO.getMid(), memberDTO); // 회원 정보 수정 서비스 호출
            response.put("result", "success");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response); // 수정 실패 시 에러 메시지 반환
        } catch (Exception e) {
            // 일반적인 예외 처리
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 회원 탈퇴

}
