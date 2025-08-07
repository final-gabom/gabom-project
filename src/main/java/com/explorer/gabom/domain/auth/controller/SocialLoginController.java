package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.oauth.dto.request.SocialLoginRequest;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.oauth.service.SocialLoginServiceFactory;
import com.explorer.gabom.global.oauth.service.SocialOAuthLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SocialLoginController {

    private final SocialLoginServiceFactory socialLoginServiceFactory;

    @PostMapping("/social-login")
    public ResponseEntity<ApiResponse<SocialLoginResponse>>socialLogin(
            @RequestBody @Valid SocialLoginRequest request) {

        SocialOAuthLoginService service = socialLoginServiceFactory.getService(request.getProvider());
        SocialLoginResponse response = service.login(request.getCode());
        return ResponseEntity.ok(ApiResponse.success("소셜 로그인에 성공하였습니다.", response));
    }
}
