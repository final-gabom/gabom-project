package com.explorer.gabom.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailRequest {
	@Email
	@NotBlank(message = "이메일을 입력해주세요.")
	@Schema(description = "로그인할 이메일")
	private final String email;
}
