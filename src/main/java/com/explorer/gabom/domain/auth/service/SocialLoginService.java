package com.explorer.gabom.domain.auth.service;


import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.oauth.dto.OAuthUserInfo;
import com.explorer.gabom.global.oauth.dto.response.SocialLoginResponse;
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

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTokenService redisTokenService;

    // OAuthUserInfo 기반 로그인 또는 신규 회원가입 후 토큰 발급
    @Transactional
    public SocialLoginResponse loginOrSignUp(OAuthUserInfo userInfo) {
        // 1. 이메일로 활성화된 유저가 있는지 조회
        User user = userRepository.findByEmailAndStatus(userInfo.getEmail(), UserStatus.ACTIVE)
                // 2. 없으면 신규 회원가입 처리
                .orElseGet(() -> createUser(userInfo));

        // 3. JWT AccessToken, RefreshToken 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

        // 4. Redis에 RefreshToken 저장 (유효기간 포함)
        redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        // 5. 로그인 응답 생성 및 반환
        return SocialLoginResponse.builder()
                .providerId(userInfo.getProviderId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //회원가입 부분 토큰생성하는 메서드
    private User createUser(OAuthUserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname() != null ? userInfo.getNickname() : "User" + System.currentTimeMillis())
                .password("")                                 // 소셜 로그인 회원가입 비밀번호 없음
                .provider(userInfo.getProvider())
                .providerId(userInfo.getProviderId())
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }
}
