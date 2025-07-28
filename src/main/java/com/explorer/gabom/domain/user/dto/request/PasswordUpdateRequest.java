package com.explorer.gabom.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordUpdateRequest {

	@NotBlank(message = "기존 비밀번호를 입력해주세요")
	private String oldPassword;
	@NotBlank(message = "새 비밀번호를 입력해주세요")
	private String newPassword;
}
