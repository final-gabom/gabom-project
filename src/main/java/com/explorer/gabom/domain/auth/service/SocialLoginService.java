package com.explorer.gabom.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.social.repository.SocialAccountRepository;
import com.explorer.gabom.domain.social.dto.OAuthUserInfo;
import com.explorer.gabom.domain.social.dto.request.SocialSignupRequest;
import com.explorer.gabom.domain.social.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.social.type.SocialProvider;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.service.UserService;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.common.SignupCommonService;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

	private final SocialAccountRepository socialAccountRepository;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final RedisTokenService redisTokenService;
	private final UserService userService;
	private final SignupCommonService signupCommonService;

	@Transactional
	public SocialLoginResponse socialLogin(OAuthUserInfo userInfo) {
		log.debug("socialLogin 호출: provider={}, providerId={}, email={}",
				  userInfo.getProvider(), userInfo.getProviderId(), userInfo.getEmail());
		SocialProvider socialProvider = userInfo.getProvider();

		// 이메일과 활성 상태로 기존 User 조회
		User userFromEmail = userRepository.findByEmailAndStatus(userInfo.getEmail(), UserStatus.ACTIVE).orElseThrow(
			() -> {
				log.warn("User not found for email: {}", userInfo.getEmail());
				return new CustomException(ErrorCode.USER_NOT_FOUND);
			}
		);
		log.debug("이메일로 조회된 유저: {}", userFromEmail.getEmail());

		// User 없으면 SocialAccount 임시 저장 (중복 없으면)
		boolean exists = socialAccountRepository.existsByProviderAndProviderId(socialProvider,
																			   userInfo.getProviderId());
		log.debug("소셜 계정 존재 여부: {}", exists);
		if (!exists) {
			SocialAccount tempAccount = SocialAccount.builder()
													 .provider(socialProvider)
													 .providerId(userInfo.getProviderId())
													 .email(userInfo.getEmail())
													 .build();
			socialAccountRepository.save(tempAccount);
			log.debug("임시 소셜 계정 저장 완료");
		}

		SocialAccount socialAccount = socialAccountRepository.findByProviderAndProviderId(socialProvider,
																						  userInfo.getProviderId())
															 .orElseThrow(() -> new CustomException(
																 ErrorCode.SOCIAL_ACCOUNT_NOT_LINKED));

		// 이미 같은 user + provider 관계가 있으면 예외 처리 또는 socialAccount 조회 다시
		socialAccount = socialAccountRepository.findByUserIdAndProvider(userFromEmail.getId(), socialProvider)
											   .orElseThrow(() -> new CustomException(
												   ErrorCode.DUPLICATED_SOCIAL_ACCOUNT));

		socialAccount = SocialAccount.builder()
									 .user(userFromEmail)
									 .provider(socialProvider)
									 .providerId(userInfo.getProviderId())
									 .email(userFromEmail.getEmail())
									 .build();
		socialAccountRepository.save(socialAccount);

		return generateTokensAndResponse(userFromEmail, userInfo.getProviderId());

	}

	@Transactional
	public SocialLoginResponse signUp(SocialSignupRequest signupRequest) {
		// 이메일 중복 확인
		userService.validateEmailNotExists(signupRequest.getEmail());

		SocialProvider provider = signupRequest.getProvider();
		String providerId = signupRequest.getProviderId();
		if (socialAccountRepository.existsByProviderAndProviderId(provider, providerId)) {
			throw new CustomException(ErrorCode.DUPLICATED_SOCIAL_ACCOUNT);
		}
		signupCommonService.validateNicknameNotExists(signupRequest.getNickname());

		// 유저 생성
		User user = signupCommonService.createUserForSocial(
			signupRequest.getEmail(),
			signupRequest.getNickname()
		);

		// 소셜 계정 연결
		signupCommonService.createAndLinkSocialAccount(user, provider, signupRequest.getProviderId(),
													   signupRequest.getEmail());

		// 토큰 생성 및 반환
		return signupCommonService.generateSocialLoginResponse(user, signupRequest.getProviderId());
	}

	private SocialLoginResponse generateTokensAndResponse(User user, String providerId) {
		String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
		String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

		redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

		return SocialLoginResponse.builder()
								  .providerId(providerId)
								  .email(user.getEmail())
								  .accessToken(accessToken)
								  .refreshToken(refreshToken)
								  .build();
	}
}
