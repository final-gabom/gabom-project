package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.oauth.dto.request.SocialLoginRequest;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
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

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    // 카카오 로그인 페이지로 이동 (리다이렉트)
    @GetMapping("/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        System.out.println("redirectUri = " + redirectUri);  // 이 값이 제대로 나오나요?
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        response.sendRedirect(kakaoAuthUrl);
    }
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        // 받은 인가 코드로 액세스 토큰 요청 및 로그인 처리 로직 작성
        System.out.println("인가 코드: " + code);

        // 예시: socialLoginServiceFactory에서 카카오 서비스 가져와서 로그인 처리
        SocialOAuthLoginService kakaoService = socialLoginServiceFactory.getService(OAuthProvider.KAKAO);
        SocialLoginResponse response = kakaoService.login(code);

        return ResponseEntity.ok(ApiResponse.success("카카오 로그인 성공", response));
    }

    @PostMapping("/social-login")
    public ResponseEntity<ApiResponse<SocialLoginResponse>>socialLogin(
            @RequestBody @Valid SocialLoginRequest request) {

        SocialOAuthLoginService service = socialLoginServiceFactory.getService(request.getProvider());
        SocialLoginResponse response = service.login(request.getCode());
        return ResponseEntity.ok(ApiResponse.success("소셜 로그인에 성공하였습니다.", response));
    }
}
