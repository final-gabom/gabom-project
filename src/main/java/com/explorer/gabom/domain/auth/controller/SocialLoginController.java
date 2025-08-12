package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.SocialSignupRequest;
import com.explorer.gabom.domain.auth.service.SocialLoginService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.domain.auth.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.auth.oauth.service.SocialLoginServiceFactory;
import com.explorer.gabom.domain.auth.oauth.service.SocialOAuthLoginService;
import com.explorer.gabom.domain.auth.oauth.type.OAuthProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @GetMapping("/{provider}")
    public void redirectToProvider(
            @PathVariable String provider,
            HttpServletResponse response) throws IOException {
        // provider 대소문자 구분없이 enum에 맞게 변환
        OAuthProvider oauthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        // 해당 provider에 맞는 로그인 서비스 객체를 팩토리에서 가져옴
        SocialOAuthLoginService service = socialLoginServiceFactory.getService(oauthProvider);
        // 해당 서비스에서 제공하는 소셜 로그인 URL을 가져옴 (ex. 카카오 로그인 페이지 URL)
        String authorizationUrl = service.getAuthorizationUrl();
        // 클라이언트에게 소셜 로그인 페이지 URL로 리다이렉트 응답
        response.sendRedirect(authorizationUrl);
    }

    //
    @GetMapping("/{provider}/callback")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> oAuthCallback(
            @PathVariable String provider,
            @RequestParam String code) {

        OAuthProvider oauthProvider = OAuthProvider.valueOf(provider.toUpperCase());
        SocialOAuthLoginService service = socialLoginServiceFactory.getService(oauthProvider);

        // SocialLoginService 에서 로그인 or 회원가입 처리
        SocialLoginResponse loginResponse = service.login(code);

        // 결과 반환
        return ResponseEntity.ok(ApiResponse.success(oauthProvider.name() + " 로그인 성공", loginResponse));
    }

    @PostMapping("/social-signup")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> signUp(
            @Valid @RequestBody SocialSignupRequest signupRequest) {


        SocialLoginResponse response = socialLoginService.signUp(signupRequest);

        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", response));
    }
}
