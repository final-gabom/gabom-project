package com.explorer.gabom.domain.user.service;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.PasswordUpdateRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.dto.response.UpdateMainTitleResponse;

import com.explorer.gabom.domain.user.entity.User;
import jakarta.validation.Valid;

public interface UserService {
	UserDto getUser(User user);

	UserDto getUser(Long userId);

	UserDto updateUser(User user, UserUpdateRequest updateRequest);

	void deleteUser(User user);


	UpdateMainTitleResponse updateMainTitle(User user, Long titleId);
  
	void updatePassword(Long userId, @Valid PasswordUpdateRequest passwordUpdateRequest);

}
