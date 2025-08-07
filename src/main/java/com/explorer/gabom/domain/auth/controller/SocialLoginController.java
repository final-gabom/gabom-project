package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.oauth.dto.request.SocialLoginRequest;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.oauth.service.SocialLoginServiceFactory;
import com.explorer.gabom.global.oauth.service.SocialOAuthLoginService;
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

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    // 카카오 로그인 페이지로 이동 (리다이렉트)
    @GetMapping("/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        response.sendRedirect(kakaoAuthUrl);
    }
    @PostMapping("/social-login")
    public ResponseEntity<ApiResponse<SocialLoginResponse>>socialLogin(
            @RequestBody @Valid SocialLoginRequest request) {

        SocialOAuthLoginService service = socialLoginServiceFactory.getService(request.getProvider());
        SocialLoginResponse response = service.login(request.getCode());
        return ResponseEntity.ok(ApiResponse.success("소셜 로그인에 성공하였습니다.", response));
    }
}
