package com.explorer.gabom.domain.social.controller;

import com.explorer.gabom.domain.auth.service.SocialLoginService;
import com.explorer.gabom.domain.social.dto.OAuthUserInfo;
import com.explorer.gabom.domain.social.dto.request.SocialSignupRequest;
import com.explorer.gabom.domain.social.type.SocialProvider;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.domain.social.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.social.service.SocialLoginServiceFactory;
import com.explorer.gabom.domain.social.service.SocialService;

import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
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
    private final SocialService socialService;
    private final SocialLoginService socialLoginService;


    // 로그인 페이지로 이동 (리다이렉트)
    @GetMapping("/{provider}")
    public void redirectToProvider(
            @PathVariable String provider,
            HttpServletResponse response) throws IOException {
        // provider 대소문자 구분없이 enum에 맞게 변환
		SocialProvider oauthProvider = SocialProvider.valueOf(provider.toUpperCase());
        // 해당 provider에 맞는 로그인 서비스 객체를 팩토리에서 가져옴
        SocialService service = socialLoginServiceFactory.getService(oauthProvider);
        // 해당 서비스에서 제공하는 소셜 로그인 URL을 가져옴 (ex. 카카오 로그인 페이지 URL)
        String authorizationUrl = service.getAuthorizationUrl();
        // 클라이언트에게 소셜 로그인 페이지 URL로 리다이렉트 응답
        response.sendRedirect(authorizationUrl);
    }

    /**
     * OAuth 소셜 로그인 콜백 핸들러
     *
     * 1. 사용자가 소셜 로그인 인증 후 리다이렉트로 전달한 코드(code)를 받아 액세스 토큰 발급
     * 2. 발급받은 액세스 토큰을 사용하여 소셜 제공자로부터 사용자 정보 조회
     * 3. 조회한 사용자 정보로 회원가입 여부 확인
     *      - 이미 가입된 사용자면 로그인 처리 후 토큰 발급
     *      - 가입되지 않은 사용자면 예외 처리 (USER_NOT_FOUND)
     * 4. 로그인 성공 시 SocialLoginResponse를 ApiResponse로 감싸서 반환
     *
     * @param provider 소셜 로그인 제공자 (KAKAO, GOOGLE 등)
     * @param code OAuth 인증 후 전달된 인가 코드
     * @return ApiResponse<SocialLoginResponse> 로그인 결과
     */
    @GetMapping("/{provider}/callback")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> oAuthCallback(
            @PathVariable String provider,
            @RequestParam String code) {
        SocialProvider oauthProvider = SocialProvider.valueOf(provider.toUpperCase());
        SocialService service = socialLoginServiceFactory.getService(oauthProvider);
        // 1. 코드로 토큰 가져오기
        String accessToken = service.getAccessToken(code);

        // 2. 토큰으로 사용자 정보 가져오기
        OAuthUserInfo userInfo = service.getOAuthUserInfoForProvider(accessToken);

        // 3. 가져온 사용자 정보로 회원가입 여부 확인 -> 로그인을 할지 회원가입을 할지 결정
        boolean wasSaved = socialLoginService.handleLogin(userInfo);

        if(wasSaved) {
            // -> 로그인
            SocialLoginResponse loginResponse = socialLoginService.socialLogin(userInfo);
            // 결과 반환
            return ResponseEntity.ok(ApiResponse.success(oauthProvider.name() + " 로그인 성공", loginResponse));
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @PostMapping("/social-signup")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> signUp(
            @Valid @RequestBody SocialSignupRequest signupRequest) {

        SocialLoginResponse response = socialLoginService.socialSignUp(signupRequest);

        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", response));
    }
}
