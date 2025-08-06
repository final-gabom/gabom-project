package com.explorer.gabom.global.oauth.service;

import com.explorer.gabom.global.oauth.dto.SocialLoginResponse;

public interface  OAuthLoginService {
    SocialLoginResponse login(String accessToken);
}
