package com.explorer.gabom.global.oauth.service;

import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.oauth.type.OAuthProvider;

public interface SocialOAuthLoginService {

    SocialLoginResponse login(String accessToken);

    OAuthProvider getProvider();

    String getAccessToken(String code);

    // 인증 URL 반환 메서드 추가
    String getAuthorizationUrl();
}
