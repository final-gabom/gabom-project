package com.explorer.gabom.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.CheckNicknameResponse;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtUtil;
import com.explorer.gabom.global.validator.PasswordValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PasswordValidator passwordValidator;
	private final JwtUtil jwtUtil;

	public SignupResponse signup(SignupRequest request) {
		// 이메일 중복 체크
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
		// 닉네임 중복 체크
		if (userRepository.existsByNickname(request.getNickname())) {
			throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
		// 비밀번호
		String encodePassword = passwordEncoder.encode(request.getPassword());

		// user
		User user = User.builder()
						.email(request.getEmail())
						.password(encodePassword)
						.nickname(request.getNickname())
						.userRole(UserRole.USER)
						.build();

		User savedUser = userRepository.save(user);

		return SignupResponse.toDto(savedUser);
	}

	// 로그인
	public LoginResponse login(LoginRequest request) {
		// 이메일로 유저 가져오기
		User user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		// 비밀번호 일치 확인
		passwordValidator.verifyMatch(request.getPassword(), user.getPassword());

		//토큰을 생성
		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUserRole());
		String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUserRole());

		return LoginResponse.toDto(accessToken, refreshToken);
	}
	// 닉네임 중복 확인
	public CheckNicknameResponse checkNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
		boolean available = !userRepository.existsByNickname(nickname);
		return new CheckNicknameResponse(available);
	}
}
