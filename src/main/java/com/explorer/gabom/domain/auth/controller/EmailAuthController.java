package com.explorer.gabom.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;
import com.explorer.gabom.domain.auth.service.EmailAuthService;
import com.explorer.gabom.domain.auth.service.PasswordResetService;
import com.explorer.gabom.global.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class EmailAuthController implements EmailAuthControllerDocs{
    private final EmailAuthService emailAuthService;
    private final PasswordResetService passwordResetService;
    // 이메일 인증 코드 전송
    @PostMapping("/email/request")
    public ResponseEntity<ApiResponse<Void>> requestEmail(@RequestBody EmailRequest request) {
        emailAuthService.sendAuthCode(request);
        return ResponseEntity.ok(ApiResponse.success("인증 코드를 이메일로 전송했습니다."));
    }
    // 이메일 인증 코드 검증
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifiedEmail(@RequestBody EmailCodeVerifyRequest request) {
        emailAuthService.verifyAuthCode((request));
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료 되었습니다."));
    }
    // 비밀번호 재설정을 위한 인증코드 이메일로 전송
    @PostMapping("password-reset/request")
    @Operation(summary = "비밀번호 재설정 인증코드 전송")
    public ResponseEntity<ApiResponse<Void>> passwordResetRequestEmail(@RequestBody PasswordResetRequest request) {
        passwordResetService.sendResetCode(request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 인증 코드를 이메일로 전송했습니다."));
    }
    // 비밀번호 재설정을 위한 인증 코드 검증 + 비밀번호 재설정
    @PostMapping("password-reset/verify")
    public ResponseEntity<ApiResponse<Void>> passwordResetVerifiedEmail(@RequestBody PasswordResetVerifyRequest resetVerifyRequest) {
        passwordResetService.verifiedResetCode(resetVerifyRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정이 완료 되었습니다. "));
    }
}
