package com.explorer.gabom.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetVerifyRequest {
    @Email
    @NotBlank(message = "이메일 입력은 필수 입니다.")
    private final String email;
    @NotBlank(message = "인증 코드 입력은 필수 입니다.")
    private final String code;
    @NotBlank(message = "새로운 비밀번호 입력은 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "비밀번호는 최소 8글자 이상, 대소문자 하나 이상 포함해야합니다.")
    private final String newPassword;
}
