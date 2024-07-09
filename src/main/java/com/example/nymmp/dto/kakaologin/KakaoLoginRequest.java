package com.example.nymmp.dto.kakaologin;

public class KakaoLoginRequest {
    private String code;

    public KakaoLoginRequest() {
    }

    public KakaoLoginRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
