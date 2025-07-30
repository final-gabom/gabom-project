package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.auth.service.EmailAuthService;
import com.explorer.gabom.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Void>> requestEmail(@RequestBody EmailRequest request) {
        emailAuthService.sendAuthCode(request);
        return ResponseEntity.ok(ApiResponse.success("인증 코드를 이메일로 발송했습니다."));
    }
}
