package com.explorer.gabom.domain.auth.dto.response;

import com.explorer.gabom.domain.user.entity.User;

import lombok.Getter;

@Getter
public class SignupResponse {
	private final Long id;

	public SignupResponse(Long id){
		this.id = id;
	}
	public static SignupResponse toDto(User user){
		return new SignupResponse(user.getId());
	}
}
