package com.explorer.gabom.domain.auth.service;


import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.oauth.dto.response.TokenResponse;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.explorer.gabom.global.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final JwtProvider jwtProvider;
    private final EmailCodeStorageService emailCodeStorageService;
    private final RedisTokenService redisTokenService;

    @Transactional
    public UserSummaryDto signup(SignupRequest request, boolean isSocial) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // 이메일 인증 체크
        if (!isSocial) {
            EmailCodeVerifyRequest verifiedRequest = EmailCodeVerifyRequest.onlyEmail(request.getEmail());
            if (!emailCodeStorageService.isEmailVerified(verifiedRequest)) {
                throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
            }
        }
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        // 비밀번호 (소셜로그인 회원가입시 비밀번호 정보가 없을 수 있음)
        String encodePassword = "";
        if (!isSocial) {
            encodePassword = passwordEncoder.encode(request.getPassword());
        }

        // user
        User user = User.builder()
                .email(request.getEmail())
                .password(encodePassword)
                .nickname(request.getNickname())
                .userRole(request.getRole())
                .provider(request.getProvider())
                .providerId(request.getProviderId())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        return UserSummaryDto.toDto(savedUser);
    }

    //회원가입 부분 토큰생성하는 메서드
    @Transactional(readOnly = true)
    public TokenResponse loginSocialGenerateToken(String email) {
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

        redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
