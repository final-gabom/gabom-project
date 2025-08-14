package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.social.dto.JwtTokens;
import com.explorer.gabom.domain.social.dto.OAuthUserInfo;
import com.explorer.gabom.domain.social.dto.request.SocialSignupRequest;
import com.explorer.gabom.domain.social.dto.response.SocialLoginResponse;
import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.social.repository.SocialAccountRepository;
import com.explorer.gabom.domain.social.type.SocialProvider;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    @Transactional
    public SocialLoginResponse socialLogin(OAuthUserInfo userInfo) {
        log.debug("socialLogin 호출: provider={}, providerId={}, email={}",
                userInfo.getProvider(), userInfo.getProviderId(), userInfo.getEmail());
        SocialProvider socialProvider = userInfo.getProvider();

        // 이메일과 활성 상태로 기존 User 조회
        User userFromEmail = userRepository.findByEmailAndStatus(userInfo.getEmail(), UserStatus.ACTIVE)
                .orElse(null);

        //  User 없으면 SocialAccount 임시 저장 후 예외 발생
        if (userFromEmail == null) {
            boolean exists = socialAccountRepository.existsByProviderTypeAndProviderId(
                    socialProvider, userInfo.getProviderId());
            if (!exists) {
                SocialAccount tempAccount = SocialAccount.builder()
                        .providerType(socialProvider)
                        .providerId(userInfo.getProviderId())
                        .email(userInfo.getEmail())
                        .build();
                socialAccountRepository.save(tempAccount);
                log.debug("신규 유저: 임시 SocialAccount 저장 완료");
            }

            // 신규 유저 → 회원가입 유도
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 기존 유저이면 SocialAccount 연결 확인 (선택 사항)
        socialAccountRepository.findByUserIdAndProviderType(userFromEmail.getId(), socialProvider)
                .orElseGet(() -> {
                            log.debug("기존 유저인데 소셜 연결 없음 → 자동 연결 시도");
                            return createAndLinkSocialAccount(
                                    userFromEmail,
                                    socialProvider,
                                    userInfo.getProviderId(),
                                    userInfo.getEmail()
                            );
                });


        // JWT 토큰 생성
        JwtTokens tokens = generateTokens(userFromEmail);

        return toTokenResponse(tokens);
    }

    //소셜회원 가입
    @Transactional
    public SocialLoginResponse signUp(SocialSignupRequest signupRequest) {


        validateNicknameNotExists(signupRequest.getNickname());

        // 유저 생성
        User user = User.ofSocial(
                signupRequest.getEmail(),
                signupRequest.getNickname()
        );

        // 소셜 계정 연결
        createAndLinkSocialAccount(user, signupRequest.getProvider(), signupRequest.getProviderId(),
                signupRequest.getEmail());
        // JWT 토큰 생성
        JwtTokens tokens = generateTokens(user);

        // 응답용 DTO로 변환 후 반환
        return toTokenResponse(tokens);
    }


    // JWT 토큰 생성만 담당
    private JwtTokens generateTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

        // Redis에 refresh token 저장
        redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        return new JwtTokens(accessToken, refreshToken);
    }

    // JwtTokens -> TokenResponse 변환
    private SocialLoginResponse toTokenResponse(JwtTokens tokens) {
        return SocialLoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .build();
    }

    public void validateNicknameNotExists(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     * 회원가입 시 신규 유저의 소셜 계정을 실제 User와 연결하고 저장합니다.
     *
     * 1️⃣ 중복 체크
     *    - 같은 소셜 제공자(provider)와 providerId가 이미 존재하면
     *      이미 연결된 계정으로 간주하여 예외 발생.
     *
     * 2️⃣ SocialAccount 객체 생성
     *    - User와 소셜 계정 정보를 연결
     *    - 이메일, provider, providerId를 SocialAccount에 저장
     *
     * 3️⃣ DB 저장
     *    - 실제 DB에 SocialAccount 저장 후 반환
     *
     * @param user       회원가입으로 생성된 User 엔티티
     * @param provider   소셜 제공자 (예: KAKAO, GOOGLE)
     * @param providerId 소셜 공급자의 고유 ID
     * @param email      소셜 계정 이메일
     * @return 저장된 SocialAccount 엔티티
     * @throws CustomException 중복된 소셜 계정인 경우
     */
    @Transactional
    public SocialAccount createAndLinkSocialAccount(User user, SocialProvider provider, String providerId, String email) {
        // 이 시점에서 duplicate 체크 해두는 게 안전
        if (socialAccountRepository.findByProviderTypeAndProviderId(provider, providerId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_SOCIAL_ACCOUNT);
        }

        SocialAccount account = SocialAccount.builder()
                .user(user)
                .email(email)
                .providerType(provider)
                .providerId(providerId)
                .build();

        return socialAccountRepository.save(account);
    }
}
