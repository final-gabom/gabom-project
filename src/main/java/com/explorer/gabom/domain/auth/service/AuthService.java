package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.CheckNicknameResponse;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.social.dto.JwtTokens;
import com.explorer.gabom.domain.social.dto.OAuthUserInfo;
import com.explorer.gabom.domain.social.dto.response.TokenResponse;
import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.social.repository.SocialAccountRepository;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.redis.service.RedisTokenService;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.explorer.gabom.global.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final JwtProvider jwtProvider;
    private final EmailCodeStorageService emailCodeStorageService;
    private final RedisTokenService redisTokenService;
    private final SocialAccountRepository socialAccountRepository;
    // 일반 회원가입
    @Transactional
    public UserSummaryDto signup(SignupRequest request) {
        // 이메일 중복 체크
        validateEmailNotExistsForRegular(request.getEmail());
        // 이메일 인증 체크
        validateEmailVerified(request.getEmail());
        // 닉네임 중복 체크
        validateNicknameNotExists(request.getNickname());
        // 유저 생성
        User user = User.ofRegular(
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
        validateEmailNotExistsForRegular(request.getEmail());
        validateEmailVerified(request.getEmail());
        validateNicknameNotExists(request.getNickname());
        // 유저 생성
        User user = User.ofRegular(
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


        return LoginResponse.toDto(accessToken, refreshToken, false);
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

    // TODO: 이걸 만들어야 함. 틀만 잡아주는 거임
    // 소셜 정보 가지고 로그인하기
    public LoginResponse socialLogin(OAuthUserInfo userInfo) {
        // SocialAccount에 UserInfo로 저장된 SocialAccount가 있는지 확인 (provider랑 providerId만 있는지 확인하면 됨. email 검사 필요X)
        // 1️⃣ SocialAccount 조회 (providerType + providerId 기준)
        Optional<SocialAccount> socialAccountOpt = socialAccountRepository
                .findByProviderTypeAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        if (socialAccountOpt.isEmpty()) {
            // 2️⃣ SocialAccount 없으면 → 신규 회원 가입 유도
            // TODO: 실제 회원가입 화면 이동 또는 임시 SocialAccount 저장
            SocialAccount tempAccount = SocialAccount.builder()
                    .providerType(userInfo.getProvider()) // providerType으로 변경
                    .providerId(userInfo.getProviderId())
                    .email(userInfo.getEmail())           // email 필요하면 저장
                    .build();

            socialAccountRepository.save(tempAccount);

            return LoginResponse.builder()
                    .isNewUser(true)
                    .build();
        }

        // 3️⃣ SocialAccount 있으면 → 기존 회원 로그인 처리
        User user = socialAccountOpt.get().getUser();
        JwtTokens tokens = generateTokens(user); // JWT 생성

        return LoginResponse.builder()
                .isNewUser(false)
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())

                .build();
        // provider -> providerType 으로 바꿔주세요
        // 없으면 회원가입 진행
        // 있으면 로그인
    }

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
    // JWT 토큰 생성만 담당
    private JwtTokens generateTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUserRole());

        // Redis에 refresh token 저장
        redisTokenService.saveRefreshToken(user.getId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        return new JwtTokens(accessToken, refreshToken);
    }

}
