package com.explorer.gabom.domain.auth.oauth.dto.request;

import com.explorer.gabom.domain.auth.oauth.type.OAuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginRequest {

    @NotNull(message = "소셜 로그인 제공자를 입력해주세요.")
    private OAuthProvider provider;

    @NotBlank(message = "인가 코드를 입력해주세요.")
    private String code;

}

