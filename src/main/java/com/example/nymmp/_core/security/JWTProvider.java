package com.example.nymmp._core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.nymmp.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTProvider {
    public static final Long EXP = 1000L * 60 * 60 * 48; // 48시간
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    private static final String SECRET = "MySecretKey";

    public static String create(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getUserId())
                .withClaim("group", user.getGroup().getGroupId())
                .sign(Algorithm.HMAC512(SECRET));
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        return JWT.require(Algorithm.HMAC512(SECRET)).build().verify(jwt.replace(TOKEN_PREFIX, ""));
    }
}
