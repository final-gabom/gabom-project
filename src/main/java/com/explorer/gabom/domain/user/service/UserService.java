package com.explorer.gabom.domain.user.service;

import com.explorer.gabom.domain.user.dto.UserDto;

public interface UserService {
	UserDto getUser(Long userId);

	void deleteUser(Long userId);
}
