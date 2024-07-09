package com.example.nymmp._core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

    private static final String SECRET_KEY = "MySecretKey";

    public static Long getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(token.replace("Bearer ", ""));
            return decodedJWT.getClaim("id").asLong();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public static Long getGroupIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET_KEY)).build().verify(token.replace("Bearer ", ""));
            return decodedJWT.getClaim("group").asLong();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }
}
