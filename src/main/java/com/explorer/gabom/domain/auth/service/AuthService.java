package com.explorer.gabom.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SignupResponse signup(SignupRequest requestDto) {
		// 이메일 중복 체크
		if (userRepository.existsByEmail(requestDto.getEmail())) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
		// 닉네임 중복 체크
		if (userRepository.existsByEmail(requestDto.getNickname())) {
			throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
		// 비밀번호
		String encodePassword = passwordEncoder.encode(requestDto.getPassword());

		// user
		User user = User.builder()
						.email(requestDto.getEmail())
						.password(encodePassword)
						.nickname(requestDto.getNickname())
						.userRole(UserRole.USER)
						.build();

		User savedUser = userRepository.save(user);

		return SignupResponse.toDto(savedUser);
	}
}
