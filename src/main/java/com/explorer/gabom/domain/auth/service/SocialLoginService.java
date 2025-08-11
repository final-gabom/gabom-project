package com.explorer.gabom.domain.auth.service;


import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.repository.SocialAccountRepository;
import com.explorer.gabom.domain.user.entity.SocialAccount;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.SocialProvider;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.oauth.dto.OAuthUserInfo;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.explorer.gabom.domain.user.type.UserRole;

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
        // OAuthProvider 타입을 도메인용 SocialProvider 타입으로 변환
        SocialProvider socialProvider = SocialProvider.fromOAuthProvider(userInfo.getProvider());
        // 이메일과 활성 상태로 기존 User 조회
        User userFromEmail = userRepository.findByEmailAndStatus(userInfo.getEmail(), UserStatus.ACTIVE).orElse(null);
        log.debug("이메일로 조회된 유저: {}", userFromEmail != null ? userFromEmail.getEmail() : "없음");
        // User가 없으면 예외 발생
        if (userFromEmail == null) {
            // User 없으면 SocialAccount 임시 저장 (중복 없으면)
            boolean exists = socialAccountRepository.existsByProviderAndProviderId(socialProvider, userInfo.getProviderId());
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
            // 회원가입 필요하므로 예외 던짐
            log.warn("User not found for email: {}", userInfo.getEmail());
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        SocialAccount socialAccount = socialAccountRepository.findByProviderAndProviderId(socialProvider, userInfo.getProviderId())
                .orElse(null);

        if (socialAccount == null) {
            // 중복 insert 방지 위해 user+provider 조합 중복 여부 확인 필요
            boolean exists = socialAccountRepository.existsByUserIdAndProvider(userFromEmail.getId(), socialProvider);
            if (exists) {
                // 이미 같은 user + provider 관계가 있으면 예외 처리 또는 socialAccount 조회 다시
                socialAccount = socialAccountRepository.findByUserIdAndProvider(userFromEmail.getId(), socialProvider)
                        .orElseThrow(() -> new CustomException(ErrorCode.DUPLICATED_SOCIAL_ACCOUNT));
            } else {
                socialAccount = SocialAccount.builder()
                        .user(userFromEmail)
                        .provider(socialProvider)
                        .providerId(userInfo.getProviderId())
                        .email(userFromEmail.getEmail())
                        .build();
                socialAccountRepository.save(socialAccount);
            }
        } else if (socialAccount.getUser() == null) {
            throw new CustomException(ErrorCode.SOCIAL_ACCOUNT_NOT_LINKED);
        }

        return generateTokensAndResponse(userFromEmail);

    }


    @Transactional
    public SocialLoginResponse signUp(SignupRequest signupRequest) {
        log.debug("signUp 호출: email={}, nickname={}, provider={}, providerId={}",
                signupRequest.getEmail(), signupRequest.getNickname(), signupRequest.getProvider(), signupRequest.getProviderId());
        SocialProvider socialProvider = SocialProvider.fromOAuthProvider(signupRequest.getProvider());

        // 이미 가입된 유저인지 확인 (이메일 중복체크)
        if (userRepository.findByEmailAndStatus(signupRequest.getEmail(), UserStatus.ACTIVE).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // SocialAccount 중복 확인 (provider + providerId)
        if (socialAccountRepository.findByProviderAndProviderId(socialProvider, signupRequest.getProviderId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_SOCIAL_ACCOUNT);
        }

        // User 생성
        User user = User.builder()
                .email(signupRequest.getEmail())
                .nickname(signupRequest.getNickname())
                .password("") // 소셜 로그인은 비밀번호 없음
                .provider(signupRequest.getProvider())
                .providerId(signupRequest.getProviderId())
                .status(UserStatus.ACTIVE)
                .userRole(UserRole.USER)
                .build();
        userRepository.save(user);

        // SocialAccount 생성 및 연결
        SocialAccount socialAccount = SocialAccount.builder()
                .user(user)
                .email(signupRequest.getEmail())
                .provider(socialProvider)
                .providerId(signupRequest.getProviderId())
                .build();
        socialAccountRepository.save(socialAccount);

        // 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

        redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        // 응답 DTO 생성 및 반환
        return SocialLoginResponse.builder()
                .providerId(signupRequest.getProviderId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    private SocialLoginResponse generateTokensAndResponse(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

        redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        return SocialLoginResponse.builder()
                .providerId(user.getProviderId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
