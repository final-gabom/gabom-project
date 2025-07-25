package com.explorer.gabom.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    @Email
    @NotBlank(message = "이메일 입력은 필수입니다.")
    private final String email;
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private final String password;
}
