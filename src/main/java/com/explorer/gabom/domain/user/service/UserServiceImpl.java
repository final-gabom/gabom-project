package com.explorer.gabom.domain.user.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDto getUser(Long userId) {
		User byIdAndStatus = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
										   .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		return UserDto.toDto(byIdAndStatus);
	}
}
