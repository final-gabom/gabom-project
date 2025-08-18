package com.explorer.gabom.domain.social.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.social.dto.OAuthUserInfo;
import com.explorer.gabom.domain.social.dto.request.SocialSignupRequest;
import com.explorer.gabom.domain.social.dto.response.SignupResponse;
import com.explorer.gabom.domain.social.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.social.repository.SocialAccountRepository;
import com.explorer.gabom.domain.social.type.SocialProvider;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.service.UserService;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.explorer.gabom.global.security.jwt.JwtTokens;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

	private final SocialAccountRepository socialAccountRepository;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final UserService userService;

	@Transactional
	public SocialLoginResponse socialLogin(SocialAccount account) {
		User user = account.getUser();

		if (user == null || user.getStatus() != UserStatus.ACTIVE) {
			log.warn("🚧 소셜 계정은 있으나 연결된 유저 없음 or 비활성화됨: accountId={}", account.getId());
			return SocialLoginResponse.signupRequired(account.getId());
		}

		// 로그인 가능
		JwtTokens tokens = jwtProvider.generateTokens(user.getId(), user.getUserRole());
		log.info("✅ 로그인 성공: userId={}", user.getId());

		return SocialLoginResponse.loginSuccess(tokens.getAccessToken(), tokens.getRefreshToken());
	}

	// 소셜회원 가입
	@Transactional
	public SignupResponse socialSignUp(SocialSignupRequest signupRequest) {
		SocialAccount tempAccount = socialAccountRepository.findById(signupRequest.getTempId()).orElseThrow(
			() -> new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR));
		userService.validateNicknameNotExists(signupRequest.getNickname());
		userService.validateEmailNotExists(tempAccount.getEmail());

		// 유저 생성
		User savedUser = userRepository.save(
			User.ofSocial(
				tempAccount.getEmail(),
				signupRequest.getNickname())
		);

		// 소셜 계정 연결
		createAndLinkSocialAccount(savedUser, tempAccount);

		return SignupResponse.toDto(savedUser);
	}

	@Transactional
	public SocialAccount createAndLinkSocialAccount(User user, SocialAccount tempAccount) {
		tempAccount.setUser(user);
		return socialAccountRepository.save(tempAccount);
	}

	public SocialAccount handleLogin(OAuthUserInfo userInfo) {
		SocialProvider provider = userInfo.getProvider();
		String providerId = userInfo.getProviderId();

		// 소셜 계정 존재 여부 확인
		return socialAccountRepository.findByProviderTypeAndProviderId(provider, providerId)
									  .orElseGet(() -> {
										  SocialAccount newAccount = SocialAccount.builder()
																				  .providerType(provider)
																				  .providerId(providerId)
																				  .email(userInfo.getEmail())
																				  .build();

										  log.info("📌 소셜 계정 임시 저장 완료: provider={} id={}", provider, providerId);
										  return socialAccountRepository.save(newAccount);
									  });
	}
}
