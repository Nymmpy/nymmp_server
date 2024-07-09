package com.example.nymmp.service;

import com.example.nymmp.dto.kakaologin.KakaoLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoLoginService {

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
}
