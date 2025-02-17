package com.example.nymmp.controller;

import com.example.nymmp._core.security.JWTProvider;
import com.example.nymmp._core.security.JwtUtil;
import com.example.nymmp._core.utils.ApiUtils;
import com.example.nymmp.dto.user.UserRequest;
import com.example.nymmp.dto.user.UserResponse;
import com.example.nymmp.model.User;
import com.example.nymmp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/check")
    public ResponseEntity<?> checkMember(@RequestBody @Valid UserRequest.EmailCheckDTO emailCheckDTO, Errors errors) {
        userService.checkSameEmail(emailCheckDTO.getEmail());
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody @Valid UserRequest.JoinDTO requestDTO, Errors errors) {

        userService.join(requestDTO);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserRequest.LoginDTO requestDTO, Errors errors) {
        UserResponse.LoginResponse response = userService.login(requestDTO);
        return ResponseEntity.ok().header(JWTProvider.HEADER, response.getJwtToken()).body(ApiUtils.success(null));
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String token) {
        Long userId = JwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserById(userId);
        UserResponse.MyPage response = new UserResponse.MyPage(user);
        return ResponseEntity.ok(ApiUtils.success(response));
    }


}
