package com.explorer.gabom.domain.auth.oauth.entity;

import com.explorer.gabom.domain.auth.oauth.type.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserInfo {
    private final String email;
    private final String nickname;
    private final OAuthProvider oAuthProvider;
}
