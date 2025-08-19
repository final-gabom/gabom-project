package com.explorer.gabom.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.CheckNicknameResponse;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.social.service.FirstLoginService;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.service.UserService;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.explorer.gabom.global.security.jwt.JwtTokens;
import com.explorer.gabom.global.validator.PasswordValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PasswordValidator passwordValidator;
	private final JwtProvider jwtProvider;
	private final EmailCodeStorageService emailCodeStorageService;
	private final RedisTokenService redisTokenService;
	private final UserService userService;
	private final FirstLoginService firstLoginService;

	// 일반 회원가입
	@Transactional
	public UserSummaryDto signup(SignupRequest request) {
		// 이메일 중복 체크
		userService.validateEmailNotExists(request.getEmail());
		// 이메일 인증 체크
		validateEmailVerified(request.getEmail());
		// 닉네임 중복 체크
		userService.validateNicknameNotExists(request.getNickname());
		//
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		// 유저 생성
		User user = User.ofRegular(
			request.getEmail(),
			request.getNickname(),
			encodedPassword,
			request.getRole()
		);
		userRepository.save(user);
		// 첫 로그인 Redis 마킹
		firstLoginService.markFirstLogin(user.getId());

		return UserSummaryDto.toDto(user);
	}

	// 포스트맨 회원가입시 테스트용
	@Transactional
	public UserSummaryDto testSignup(SignupRequest request) {
		userService.validateEmailNotExists(request.getEmail());
		validateEmailVerified(request.getEmail());
		userService.validateNicknameNotExists(request.getNickname());
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		// 유저 생성
		User user = User.ofRegular(
			request.getEmail(),
			request.getNickname(),
			encodedPassword,
			request.getRole()
		);
		userRepository.save(user);
		// 첫 로그인 Redis 마킹
		firstLoginService.markFirstLogin(user.getId());

		return UserSummaryDto.toDto(user);
	}

	// 로그인
	@Transactional
	public LoginResponse login(LoginRequest request) {
		// 이메일로 유저 가져오기
		User user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		// 비밀번호 일치 확인
		passwordValidator.verifyMatch(request.getPassword(), user.getPassword());
		// 로그인 여부 판별 (Redis에서 삭제하며 확인)
		boolean newUser = firstLoginService.consumeFirstLogin(user.getId());
		//토큰을 생성
		JwtTokens jwtTokens = jwtProvider.generateTokens(user.getId(), user.getUserRole());

		return LoginResponse.toDto(jwtTokens.getAccessToken(), jwtTokens.getRefreshToken(), newUser);
	}

	// 닉네임 중복 확인
	@Transactional
	public CheckNicknameResponse checkNickname(String nickname) {
		userService.validateNicknameNotExists(nickname);
		return new CheckNicknameResponse(true);
	}

	// TODO 위치 이동해야함
	public JwtTokens reissue(String refreshToken) {
		// 1. Refresh Token 유효성 검사
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		// 2. userId 추출
		Long userId = Long.parseLong(jwtProvider.getUserIdFromToken(refreshToken));

		// 3. Redis 저장된 refresh token 확인
		String storedToken = redisTokenService.getRefreshToken(userId);
		if (storedToken == null) {
			throw new CustomException(ErrorCode.EMPTY_TOKEN);
		}
		if (!storedToken.equals(refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		// 4. 사용자 조회
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 5. 새 토큰 생성 후 반환
		return jwtProvider.generateTokens(user.getId(), user.getUserRole());
	}

	public void validateEmailVerified(String email) {
		EmailCodeVerifyRequest request = new EmailCodeVerifyRequest(email, null);
		if (!emailCodeStorageService.isEmailVerified(request)) {
			throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
		}
	}
}
