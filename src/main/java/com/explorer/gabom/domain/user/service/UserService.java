package com.explorer.gabom.domain.user.service;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.dto.response.UpdateMyTitleResponse;

public interface UserService {
	UserDto getUser(Long userId);

	UserDto updateUser(Long userId, UserUpdateRequest updateRequest);

	void deleteUser(Long userId);

	UpdateMyTitleResponse updateMyTitle(Long userId, Long titleId);
}
