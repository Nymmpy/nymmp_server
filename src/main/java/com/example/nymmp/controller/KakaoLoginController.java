package com.example.nymmp.controller;

import com.example.nymmp.service.KakaoLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/kakao-login")
public class KakaoLoginController {

    private static final Logger logger = LoggerFactory.getLogger(KakaoLoginController.class);

    @Autowired
    private KakaoLoginService kakaoLoginService;

    @GetMapping
    @CrossOrigin(origins = "http://localhost:*")
    public ResponseEntity<Void> redirectToKakao() {
        String kakaoAuthUrl = kakaoLoginService.getAuthorizationUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(UriComponentsBuilder.fromHttpUrl(kakaoAuthUrl).build().toUri());

        logger.debug("Redirecting to Kakao with URL: {}", kakaoAuthUrl);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 콜백 URL 처리
    @GetMapping("/callback")
    public ResponseEntity<String> handleKakaoCallback(@RequestParam String code) {
        try {
            String accessToken = kakaoLoginService.getAccessToken(code);
            return ResponseEntity.ok(accessToken);
        } catch (Exception e) {
            logger.error("Failed to handle Kakao callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
