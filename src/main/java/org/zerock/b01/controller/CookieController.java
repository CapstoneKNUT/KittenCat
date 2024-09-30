package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import javax.servlet.http.Cookie;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.PlaceDTO;
import org.zerock.b01.dto.PlaceSearchDTO;
import org.zerock.b01.service.PlaceService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/")
@Log4j2
@RequiredArgsConstructor
public class CookieController {
    @GetMapping("/set-cookie")
    public ResponseEntity<?> setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("username", "YeChan");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 동안 유효
        cookie.setPath("/"); // 전체 경로에서 사용 가능
        response.addCookie(cookie);

        return ResponseEntity.ok("Cookie has been set");
    }

    @PostMapping("/set-cookie")
    public ResponseEntity<?> setCookie(@RequestBody String username, HttpServletResponse response) {
        Cookie cookie = new Cookie("username", username);
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 동안 유효
        cookie.setPath("/"); // 전체 경로에서 사용 가능
        response.addCookie(cookie);

        return ResponseEntity.ok("Username cookie has been set to: " + username);
    }

}
