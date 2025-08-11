package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.service.SocialLoginService;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.oauth.dto.request.SocialLoginRequest;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.oauth.dto.response.TokenResponse;
import com.explorer.gabom.global.oauth.service.SocialLoginServiceFactory;
import com.explorer.gabom.global.oauth.service.SocialOAuthLoginService;
import com.explorer.gabom.global.oauth.type.OAuthProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SocialLoginController {

    private final SocialLoginServiceFactory socialLoginServiceFactory;
    private final SocialLoginService socialLoginService;

    // 로그인 페이지로 이동 (리다이렉트)
    public void redirectToProvider(
            @PathVariable String provider,
            HttpServletResponse response) throws IOException {
        // provider 대소문자 구분없이 enum에 맞게 변환
        OAuthProvider oauthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        SocialOAuthLoginService service = socialLoginServiceFactory.getService(oauthProvider);
        String authorizationUrl = service.getAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }
    // 인가 코드 받고 로그인 or 회원가입 + JWT 토큰 발급 응답
    @GetMapping("/{provider}/callback")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> oAuthCallback(
            @PathVariable String provider,
            @RequestParam String code) {

        OAuthProvider oauthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        SocialOAuthLoginService service = socialLoginServiceFactory.getService(oauthProvider);

        // 로그인 및 회원가입 통합 처리 후 토큰 발급까지 수행
        SocialLoginResponse loginResponse = service.login(code);

        return ResponseEntity.ok(ApiResponse.success(oauthProvider.name() + " 로그인 성공", loginResponse));
    }
}
