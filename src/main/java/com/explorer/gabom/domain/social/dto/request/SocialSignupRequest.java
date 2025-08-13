package com.explorer.gabom.domain.social.dto.request;

import com.explorer.gabom.domain.social.type.SocialProvider;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private SocialProvider provider; // KAKAO, GOOGLE 등

    @NotBlank
    private String providerId;
}
