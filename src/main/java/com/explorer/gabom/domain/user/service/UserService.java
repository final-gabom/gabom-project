package com.explorer.gabom.domain.user.service;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;

public interface UserService {
	UserDto getUser(Long userId);
	UserDto updateUser(Long userId, UserUpdateRequest updateRequest);
}
