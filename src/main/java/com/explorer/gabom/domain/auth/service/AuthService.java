package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.CheckNicknameResponse;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.common.SignupCommonService;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.domain.social.dto.response.TokenResponse;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.explorer.gabom.global.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final JwtProvider jwtProvider;
    private final EmailCodeStorageService emailCodeStorageService;
    private final RedisTokenService redisTokenService;
    private final SignupCommonService signupCommonService;

    @Transactional
    public UserSummaryDto signup(SignupRequest request) {

        // 검증 (SignupCommonService 활용)
        // 이메일 중복 체크
        signupCommonService.validateEmailNotExistsForRegular(request.getEmail());
        // 이메일 인증 체크
        signupCommonService.validateEmailVerified(request.getEmail());
        // 닉네임 중복 체크
        signupCommonService.validateNicknameNotExists(request.getNickname());

        // 유저 생성
        User user = signupCommonService.createUserForRegular(
                request.getEmail(),
                request.getNickname(),
                request.getPassword(),
                request.getRole()
        );
        return UserSummaryDto.toDto(user);
    }

    // 포스트맨 회원가입시 테스트용
    @Transactional
    public UserSummaryDto testSignup(SignupRequest request) {
        // 검증 (SignupCommonService 활용)
        // 이메일 중복 체크
        signupCommonService.validateEmailNotExistsForRegular(request.getEmail());
        // 이메일 인증 체크
        signupCommonService.validateEmailVerified(request.getEmail());
        // 닉네임 중복 체크
        signupCommonService.validateNicknameNotExists(request.getNickname());

        // 유저 생성
        User user = signupCommonService.createUserForRegular(
                request.getEmail(),
                request.getNickname(),
                request.getPassword(),
                request.getRole()
        );
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

        //토큰을 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

        return LoginResponse.toDto(accessToken, refreshToken);
    }

    // 닉네임 중복 확인
    @Transactional
    public CheckNicknameResponse checkNickname(String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);
        if (exists) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        return new CheckNicknameResponse(true);
    }

    public TokenResponse reissue(String refreshToken) {
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

        // 5. 새 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(userId, user.getUserRole());
        String newRefreshToken = jwtProvider.createRefreshToken(userId, user.getUserRole());

        // 6. Redis 갱신
        redisTokenService.saveRefreshToken(userId, newRefreshToken, jwtProvider.getRefreshTokenExpiration());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
