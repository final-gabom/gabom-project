package com.explorer.gabom.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailCodeVerifyRequest {
    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    @Schema(description = "인증코드 전송받은 이메일")
    private final String email;
    @NotBlank(message = "인증코드 입력은 필수입니다.")
    @Schema(description = "이메일로 전송받은 코드")
    private final String code;

    public static EmailCodeVerifyRequest onlyEmail(String email) {
        return new EmailCodeVerifyRequest(email,null);
    }
}
