package com.explorer.gabom.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetRequest {
    @Email
    @NotBlank(message = "이메일을 입력해 주세요.")
    private final String email;
}
