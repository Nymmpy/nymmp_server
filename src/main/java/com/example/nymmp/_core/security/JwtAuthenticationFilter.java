package com.example.nymmp._core.security;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.nymmp.model.User;
import com.example.nymmp.repository.UserJPARepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final UserJPARepository userJPARepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserJPARepository userJPARepository) {
        super(authenticationManager);
        this.userJPARepository = userJPARepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String prefixJwt = request.getHeader("Authorization");
        if (prefixJwt == null) {
            chain.doFilter(request, response);
        } else {
            String jwt = prefixJwt.replace("Bearer ", "");

            try {
                log.debug("디버그 : 토큰 있음");
                DecodedJWT decodedJWT = JWTProvider.verify(jwt);
                Long userId = decodedJWT.getClaim("id").asLong();
                User user = userJPARepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
                CustomUserDetails myUserDetails = new CustomUserDetails(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(myUserDetails, myUserDetails.getPassword(), myUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("디버그 : 인증 객체 만들어짐");
            } catch (SignatureVerificationException e) {
                log.error("토큰 검증 실패", e);
            } catch (TokenExpiredException e) {
                log.error("토큰 만료됨", e);
            } finally {
                chain.doFilter(request, response);
            }
        }
    }
}
