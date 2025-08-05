package com.explorer.gabom.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordUpdateRequest {

	@Schema(description = "기존 비밀번호", example = "oldPassword123!")
	@NotBlank(message = "기존 비밀번호를 입력해주세요")
	private String oldPassword;

	@Schema(description = "새 비밀번호", example = "newPassword456!")
	@NotBlank(message = "새 비밀번호를 입력해주세요")
	private String newPassword;
}
