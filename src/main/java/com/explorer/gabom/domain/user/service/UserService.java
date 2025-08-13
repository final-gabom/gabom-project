package com.explorer.gabom.domain.user.service;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.PasswordUpdateRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.dto.response.UpdateMainTitleResponse;

import com.explorer.gabom.domain.user.entity.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface UserService {
	UserDto getUser(User user);

	UserDto getUser(Long userId);

	UserDto updateUser(User user, UserUpdateRequest updateRequest);

	void deleteUser(User user);

	UpdateMainTitleResponse updateMainTitle(User user, Long titleId);

	void updatePassword(User user, @Valid PasswordUpdateRequest passwordUpdateRequest);

	AddressDto updateUserAddress(User user, AddressRequest request);

	void validateEmailNotExists(@Email @NotBlank(message = "이메일 입력은 필수입니다.") String email);

}