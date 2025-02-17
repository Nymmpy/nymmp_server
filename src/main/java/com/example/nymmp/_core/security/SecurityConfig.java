package com.example.nymmp._core.security;

import com.example.nymmp._core.exception.Exception401;
import com.example.nymmp._core.exception.Exception403;
import com.example.nymmp._core.utils.FilterResponseUtils;
import com.example.nymmp.repository.UserJPARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserJPARepository userJPARepository;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    public SecurityConfig(UserJPARepository userJPARepository,
                          AuthenticationConfiguration authenticationConfiguration) {
        this.userJPARepository = userJPARepository;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .cors(cors -> cors.configurationSource(this.configurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("인증되지 않은 사용자가 자원에 접근하려 합니다 : " + authException.getMessage());
                            FilterResponseUtils.unAuthorized(response, new Exception401("인증되지 않았습니다"));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("권한이 없는 사용자가 자원에 접근하려 합니다 : " + accessDeniedException.getMessage());
                            FilterResponseUtils.forbidden(response, new Exception403("권한이 없습니다"));
                        })
                )
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("*").permitAll());
        http.addFilter(jwtAuthenticationFilter());
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(authenticationManager(), userJPARepository);
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("http://localhost:*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
