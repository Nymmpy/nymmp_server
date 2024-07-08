package com.example.nymmp._core.security;

import com.example.nymmp._core.exception.Exception401;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.nymmp.model.User;
import com.example.nymmp.repository.UserJPARepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserJPARepository userJPARepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userJPARepository.findByEmail(username).orElseThrow(() -> new Exception401("인증되지 않았습니다"));
        return new CustomUserDetails(user);
    }

    public CustomUserDetailsService(UserJPARepository userJPARepository) {
        this.userJPARepository = userJPARepository;
    }
}
