package com.explorer.gabom.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    @Email
    @NotBlank(message = "이메일 입력은 필수입니다.")
    @Schema(description = "로그인시 사용할 이메일")
    private final String email;
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Schema(description = "로그인 시 사용할 비밀번호")
    private final String password;
}
