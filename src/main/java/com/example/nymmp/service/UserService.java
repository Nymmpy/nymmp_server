package com.example.nymmp.service;

import com.example.nymmp._core.exception.Exception401;
import com.example.nymmp._core.exception.Exception403;
import com.example.nymmp._core.exception.Exception404;
import com.example.nymmp._core.exception.Exception500;
import com.example.nymmp._core.security.JWTProvider;
import com.example.nymmp.dto.user.UserRequest;
import com.example.nymmp.dto.user.UserResponse;
import com.example.nymmp.model.Group;
import com.example.nymmp.model.User;
import com.example.nymmp.repository.GroupRepository;
import com.example.nymmp.repository.UserJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserJPARepository userJPARepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void join(UserRequest.JoinDTO requestDTO) {
        if (userJPARepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new Exception403("이미 존재하는 이메일입니다.");
        }
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
        Group group = groupRepository.findByGroupName(requestDTO.getGroupName())
                .orElseThrow(() -> new Exception404("그룹을 찾을 수 없습니다: " + requestDTO.getGroupName()));
        User user = requestDTO.toEntity(encodedPassword, group);
        try {
            userJPARepository.save(user);
        } catch (Exception e) {
            throw new Exception500("알 수 없는 서버 오류가 발생했습니다.");
        }
    }

    public UserResponse.LoginResponse login(UserRequest.LoginDTO requestDTO) {
        User user = userJPARepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new Exception404("이메일을 찾을 수 없습니다: " + requestDTO.getEmail())
        );
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new Exception401("비밀번호가 일치하지 않습니다.");
        }
        String jwt = JWTProvider.create(user);
        String redirectUrl = "/home";

        return new UserResponse.LoginResponse(jwt, redirectUrl);
    }

    public void checkSameEmail(String email) {
        Optional<User> userOptional = userJPARepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new Exception403("이미 존재하는 이메일입니다: " + email);
        }
    }
}
