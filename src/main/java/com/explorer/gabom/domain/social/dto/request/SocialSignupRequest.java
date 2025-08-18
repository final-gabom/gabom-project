package com.explorer.gabom.domain.social.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SocialSignupRequest {

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    private String nickname;

    @NotNull(message = "임시 ID는 필수입니다.")
    private Long tempId;
}
