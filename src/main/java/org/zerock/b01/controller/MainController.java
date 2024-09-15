package org.zerock.b01.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Log4j2
public class MainController {

    @GetMapping("/main")
    public ResponseEntity<Map<String, String>> mainGET() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to the main page!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = new HashMap<>();
        response.put("redirect", "/index.html");
        return ResponseEntity.ok(response);
    }
}
