package com.explorer.gabom.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "비밀번호 재설정 인증 코드 검증 요청 정보")
public class PasswordResetRequest {
    @Email
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Schema(description = "비밀번호 재설정 시 사용할 이메일")
    private final String email;
}
