package com.example.nymmp.service;

import com.example.nymmp._core.security.JWTProvider;
import com.example.nymmp.controller.KakaoLoginController;
import com.example.nymmp.dto.kakaologin.KakaoLoginResponse;
import com.example.nymmp.model.User;
import com.example.nymmp.repository.UserJPARepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoLoginService {
    private static final Logger logger = LoggerFactory.getLogger(KakaoLoginController.class);
    @Autowired
    UserJPARepository userJPARepository;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAuthorizationUrl() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize";
        return UriComponentsBuilder.fromHttpUrl(kakaoAuthUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .toUriString();
    }

    public String getAccessToken(String code) {
        String kakaoTokenUrl = "https://kauth.kakao.com/oauth/token";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(kakaoTokenUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code);

        RestTemplate restTemplate = new RestTemplate();
        KakaoLoginResponse response = restTemplate.postForObject(builder.toUriString(), null, KakaoLoginResponse.class);
        return response.getAccessToken();
    }
    public Map<String, Object> userAuthentication(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);

        logger.debug("${}",response.getBody());
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                Long id = rootNode.path("id").asLong();
                String nickname = rootNode.path("properties").path("nickname").asText();

                User user = userJPARepository.findByKakaoId(id).orElse(null);

                logger.debug("{}",nickname);
                if (user == null) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "register");
                    result.put("kakao_id", id);
                    result.put("nickname", nickname);
                    return result;
                } else {

                    String jwtToken = JWTProvider.create(user);
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "authenticated");
                    result.put("token", jwtToken);
                    return result;
                }

            } catch (Exception e) {
                logger.error("Failed to parse Kakao user info", e);
                throw new RuntimeException("Failed to parse Kakao user info", e);
            }
        } else {
            logger.error("Failed to authenticate with Kakao: " + response.getStatusCode());
            throw new RuntimeException("Failed to authenticate with Kakao: " + response.getStatusCode());
        }
    }
}
