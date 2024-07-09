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

import java.util.Map;

@RestController
@RequestMapping("/kakao-login")
public class KakaoLoginController {

    private static final Logger logger = LoggerFactory.getLogger(KakaoLoginController.class);

    @Autowired
    private KakaoLoginService kakaoLoginService;

    @GetMapping
    @CrossOrigin(origins = "http://localhost:*")
    public ResponseEntity<String> redirectToKakao() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(kakaoAuthUrl)
                .queryParam("client_id", kakaoLoginService.getClientId())
                .queryParam("redirect_uri", kakaoLoginService.getRedirectUri())
                .queryParam("response_type", "code");

        String redirectUrl = builder.toUriString();

        logger.debug("Redirecting to Kakao with URL: {}", redirectUrl);

        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/callback")
    @CrossOrigin(origins = "http://localhost:*")
    public ResponseEntity<?> handleKakaoCallback(@RequestParam String code) {
        try {
            logger.debug("Before Authentication :{}",code);


            String accessToken = kakaoLoginService.getAccessToken(code);
            Map<String, Object> authResult = kakaoLoginService.userAuthentication(accessToken);

            logger.debug("After Authentication :{}", authResult);

            // 사용자 인증 결과에 따라 리디렉션
            if ("authenticated".equals(authResult.get("status"))) {
                String jwtToken = (String) authResult.get("token");
                return ResponseEntity.ok(jwtToken); // JWT 토큰을 반환
            } else {
                // 회원가입 페이지로 리디렉션
                logger.debug("register");
                return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                        .header(HttpHeaders.LOCATION, "http://localhost:54075/#/signup")
                        .build();
            }
        } catch (Exception e) {
            logger.error("Failed to handle Kakao callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
