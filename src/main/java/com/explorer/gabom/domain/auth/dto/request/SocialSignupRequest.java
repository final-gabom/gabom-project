package com.explorer.gabom.domain.auth.dto.request;

import com.explorer.gabom.global.oauth.type.OAuthProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SocialSignupRequest {

    @Email
    @NotBlank(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    private String nickname;

    @NotBlank
    private OAuthProvider provider; // KAKAO, GOOGLE 등

    @NotBlank
    private String providerId;
}
