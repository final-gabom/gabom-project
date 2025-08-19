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
	private final FirstLoginService firstLoginService;

	/**
	 * 소셜 계정 기반 로그인 처리
	 * - 소셜 계정과 연결된 사용자(User)를 확인하고, 로그인 가능한 상태인지 검증 후 JWT 토큰을 발급한다.
	 * - 만약 연결된 유저가 없거나 비활성화 상태라면 회원가입이 필요함을 응답한다.
	 * - 로그인 유저가 첫 로그인인지 아닌지 (redis)에서 확인한다.
	 *
	 * @param account 소셜 계정 정보 (소셜 로그인 후 조회된 SocialAccount 엔티티)
	 * @return 로그인 결과를 담은 SocialLoginResponse
	 */
	@Transactional
	public SocialLoginResponse socialLogin(SocialAccount account) {
		// 1. 소셜 계정에 연결된 User 엔티티 가져오기
		User user = account.getUser();
		// 2. 연결된 유저가 없거나, 유저 상태가 ACTIVE가 아닌 경우 (비활성화 / 탈퇴 등)
		if (user == null || user.getStatus() != UserStatus.ACTIVE) {
			log.warn("소셜 계정은 있으나 연결된 유저 없음 or 비활성화됨: accountId={}", account.getId());
			return SocialLoginResponse.signupRequired(account.getId());
		}
		// 3. 이번이 첫 로그인인지 Redis에서 소비(삭제)하며 판별
		boolean newUser = firstLoginService.consumeFirstLogin(user.getId());
		// 4. 정상적으로 로그인 가능한 경우 → JWT 토큰 생성
		JwtTokens tokens = jwtProvider.generateTokens(user.getId(), user.getUserRole());
		log.info("로그인 성공: userId={}", user.getId(),newUser);

		return SocialLoginResponse.loginSuccess(tokens.getAccessToken(), tokens.getRefreshToken(),newUser);
	}

	/**
	 * 소셜 회원 가입 처리
	 * - 소셜 로그인 시 기존 유저가 없어 회원가입이 필요한 경우 호출됨
	 * - 임시 소셜 계정(tempAccount)와 입력받은 정보를 기반으로 User를 생성하고 소셜 계정과 연결한다.
	 * - 회원가입시 첫 로그인인지 설정
	 *
	 * @param signupRequest 클라이언트에서 전달된 회원가입 요청 DTO (tempId, nickname 등 포함)
	 * @return 회원가입 완료된 사용자 정보를 담은 SignupResponse DTO
	 */
	@Transactional
	public SignupResponse socialSignUp(SocialSignupRequest signupRequest) {
		// 1. 콜백 단계에서 생성된 임시 소셜 계정(tempAccount) 조회
		SocialAccount tempAccount = socialAccountRepository.findById(signupRequest.getTempId()).orElseThrow(
			() -> new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR));
		// 2. 닉네임 중복 여부 검증
		userService.validateNicknameNotExists(signupRequest.getNickname());
		// 3. 이메일 중복 여부 검증
		userService.validateEmailNotExists(tempAccount.getEmail());

		// 4. User 엔티티 생성 및 저장 (소셜 회원 전용 생성 메서드 사용)
		User savedUser = userRepository.save(
			User.ofSocial(
				tempAccount.getEmail(),
				signupRequest.getNickname())
		);

		// 5. 생성된 User와 소셜 계정 연결
		createAndLinkSocialAccount(savedUser, tempAccount);
		// 6. 첫 로그인 설정
		firstLoginService.markFirstLogin(savedUser.getId());
		log.info("첫 로그인");
		// 7. 회원가입 완료 후 응답 DTO 반환
		return SignupResponse.toDto(savedUser);
	}
	/**
	 * 소셜 계정을 실제 회원(User)과 연결하는 메서드
	 * - socialSignUp 과정에서 생성된 User를
	 *   기존 임시 소셜 계정(tempAccount)에 매핑한다.
	 *
	 * @param user        새로 생성된 User 엔티티
	 * @param tempAccount 소셜 로그인 콜백 단계에서 생성된 임시 SocialAccount 엔티티
	 * @return User와 연결된 SocialAccount 엔티티
	 */
	@Transactional
	public SocialAccount createAndLinkSocialAccount(User user, SocialAccount tempAccount) {
		// 1. 임시 소셜 계정에 실제 User 매핑
		tempAccount.setUser(user);
		// 2. DB에 업데이트된 소셜 계정 저장 후 반환
		return socialAccountRepository.save(tempAccount);
	}
	/**
	 * 소셜 로그인 시, 전달받은 사용자 정보(OAuthUserInfo)를 기반으로
	 * DB에 소셜 계정이 이미 존재하는지 확인하고 처리하는 메서드
	 *
	 * 처리 흐름:
	 * 1. provider + providerId 로 기존 소셜 계정이 존재하는지 조회
	 * 2. 존재하면 해당 SocialAccount 반환
	 * 3. 존재하지 않으면, 새로운 SocialAccount를 "임시 계정"으로 생성 후 저장
	 *
	 *  주의:
	 * - 이 시점에서는 User 엔티티와 연결되지 않은 상태일 수 있음
	 * - 실제 회원가입이 완료되어야 User ↔ SocialAccount 매핑이 이루어짐
	 *
	 *  * @param userInfo 소셜 로그인 제공자로부터 조회한 사용자 정보 DTO
	 *  * @return DB 조회한 기존 SocialAccount, 없으면 새로 생성된 SocialAccount
	 *  */
	public SocialAccount handleLogin(OAuthUserInfo userInfo) {
		// 카카오, 구글 등 소셜 제공자
		SocialProvider provider = userInfo.getProvider();
		// 소셜에서 제공하는 유저 고유 ID
		String providerId = userInfo.getProviderId();

		// 소셜 계정 존재 여부 확인
		return socialAccountRepository.findByProviderTypeAndProviderId(provider, providerId)
									  // 2. 없으면 새로운 소셜 계정 생성 (임시 저장)
									  .orElseGet(() -> {
										  SocialAccount newAccount = SocialAccount.builder()
																				  .providerType(provider)      // 소셜 제공자
																				  .providerId(providerId)      // 소셜에서 부여한 고유 ID
																				  .email(userInfo.getEmail())  // 소셜에서 가져온 이메일
																				  .build();

										  log.info("소셜 계정 임시 저장 완료: provider={} id={}", provider, providerId);
										  return socialAccountRepository.save(newAccount);
									  });
	}
}
