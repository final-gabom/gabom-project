package com.explorer.gabom.domain.social.dto;

import com.explorer.gabom.domain.social.type.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class OAuthUserInfo {
    private SocialProvider provider;
    private String providerId;
    private String email;

    public OAuthUserInfo toDto(SocialProvider provider, String providerId, String email) {
        return OAuthUserInfo.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .build();
    }
}