package com.explorer.gabom.domain.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {
	@Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하만 가능합니다.")
	private final String nickname;
	private final String address;
	private final Double lat;
	private final Double lng;
	private final String profileImgId;
}