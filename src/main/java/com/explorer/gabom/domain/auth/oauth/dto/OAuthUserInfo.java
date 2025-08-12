package com.explorer.gabom.domain.auth.oauth.dto;

import com.explorer.gabom.domain.auth.oauth.type.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthUserInfo {
    private OAuthProvider provider;
    private String providerId;
    private String email;
    private String Nickname;

    // 닉네임 없이 생성 가능하도록 오버로딩
    public OAuthUserInfo(OAuthProvider provider, String providerId, String email) {
        this(provider, providerId, email, null);
    }
}
