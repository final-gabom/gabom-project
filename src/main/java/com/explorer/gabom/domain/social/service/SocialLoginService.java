package com.explorer.gabom.domain.social.service;

import com.explorer.gabom.domain.social.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.social.type.SocialProvider;

public interface SocialLoginService {

    SocialLoginResponse login(String accessToken);

    SocialProvider getProvider();

    String getAccessToken(String code);

    // 인증 URL 반환 메서드 추가
    String getAuthorizationUrl();
}
