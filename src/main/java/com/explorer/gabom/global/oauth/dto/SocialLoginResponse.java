package com.explorer.gabom.global.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class SocialLoginResponse {

    private final String providerId;  // 카카오의 사용자 id
    private final String email;
    private final String accessToken;
    private final String refreshToken;
}
