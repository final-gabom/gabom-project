package com.explorer.gabom.global.common;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.social.repository.SocialAccountRepository;
import com.explorer.gabom.domain.auth.service.EmailCodeStorageService;
import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.social.type.SocialProvider;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.domain.social.dto.response.SocialLoginResponse;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupCommonService {
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final EmailCodeStorageService emailCodeStorageService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    // ---------- 검증(공통) ----------
    public void validateEmailNotExistsForRegular(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    public void validateEmailVerified(String email) {
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest(email, null);
        if (!emailCodeStorageService.isEmailVerified(request)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    public void validateNicknameNotExists(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    public void validateEmailNotExistsForSocial(String email) {
        if (userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }
    }

    public void validateSocialAccountNotExists(SocialProvider provider, String providerId) {
        if (socialAccountRepository.findByProviderAndProviderId(provider, providerId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_SOCIAL_ACCOUNT);
        }
    }

    // ---------- 생성(공통) ----------
    @Transactional
    public User createUserForRegular(String email, String nickname, String rawPassword, UserRole role) {
        String encoded = passwordEncoder.encode(rawPassword);
        User user = User.builder()
                .email(email)
                .password(encoded)
                .nickname(nickname)
                .userRole(role)
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User createUserForSocial(String email, String nickname) {
        User user = User.builder()
                .email(email)
                .password("") // 소셜로그인 비밀번호 없음
                .nickname(nickname)
                .userRole(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public SocialAccount createAndLinkSocialAccount(User user, SocialProvider provider, String providerId, String email) {
        // 이 시점에서 duplicate 체크 해두는 게 안전
        if (socialAccountRepository.findByProviderAndProviderId(provider, providerId).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_SOCIAL_ACCOUNT);
        }

        SocialAccount account = SocialAccount.builder()
                .user(user)
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .build();

        try {
            return socialAccountRepository.save(account);
        } catch (DataIntegrityViolationException ex) {
            log.warn("SocialAccount save 실패: duplicate? provider={}, providerId={}", provider, providerId);
            throw new CustomException(ErrorCode.DUPLICATED_SOCIAL_ACCOUNT);
        }
    }

    // ---------- 토큰 생성 & 응답 ----------
    public SocialLoginResponse generateSocialLoginResponse(User user, String providerId) {
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());
        redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        return SocialLoginResponse.builder()
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
