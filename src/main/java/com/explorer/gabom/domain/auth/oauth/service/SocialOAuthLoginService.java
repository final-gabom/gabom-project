package com.explorer.gabom.domain.auth.oauth.service;

import com.explorer.gabom.domain.auth.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.auth.oauth.type.OAuthProvider;

public interface SocialOAuthLoginService {

    SocialLoginResponse login(String accessToken);

    OAuthProvider getProvider();

    String getAccessToken(String code);

    // 인증 URL 반환 메서드 추가
    String getAuthorizationUrl();
}
