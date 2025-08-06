package com.explorer.gabom.domain.auth.dto.response;

import com.explorer.gabom.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SignupResponse {
	@Schema(description = "회원가입된 ID")
	private final Long id;

	public SignupResponse(Long id){
		this.id = id;
	}
	public static SignupResponse toDto(User user){
		return new SignupResponse(user.getId());
	}
}
