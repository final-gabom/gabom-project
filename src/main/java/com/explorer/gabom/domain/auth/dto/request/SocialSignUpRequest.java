package com.explorer.gabom.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SocialSignUpRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    private String provider; // KAKAO, GOOGLE 등

    @NotBlank
    private String providerId;
}
