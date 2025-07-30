package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.CheckNicknameResponse;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.auth.service.AuthService;
import com.explorer.gabom.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest requestDto) {
        SignupResponse response = authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입을 성공했습니다.", response));
    }

    // 포스트맨 회원가입시 테스트용
    @PostMapping("/test/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> testSignup(@RequestBody @Valid SignupRequest requestDto) {
        SignupResponse response = authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입을 성공했습니다.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그인을 성공했습니다.", response));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<CheckNicknameResponse>> checkNickname(@RequestParam String nickname) {
        CheckNicknameResponse response = authService.checkNickname(nickname);
        return ResponseEntity.ok(ApiResponse.success("닉네임 중복확인을 완료하였습니다.", response));
    }

}
