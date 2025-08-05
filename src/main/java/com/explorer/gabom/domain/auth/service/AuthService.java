package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.CheckNicknameResponse;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
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

    @Transactional
    public UserSummaryDto signup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // 이메일 인증 체크
        EmailCodeVerifyRequest verifiedRequest = EmailCodeVerifyRequest.onlyEmail(request.getEmail());
        if (!emailCodeStorageService.isEmailVerified(verifiedRequest)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        // 비밀번호
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // user
        User user = User.builder()
                .email(request.getEmail())
                .password(encodePassword)
                .nickname(request.getNickname())
                .userRole(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return UserSummaryDto.toDto(savedUser);
    }

    // 포스트맨 회원가입시 테스트용
    @Transactional
    public UserSummaryDto testSignup(SignupRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        // 비밀번호
        String encodePassword = passwordEncoder.encode(request.getPassword());

        // user
        User user = User.builder()
                .email(request.getEmail())
                .password(encodePassword)
                .nickname(request.getNickname())
                .userRole(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return UserSummaryDto.toDto(savedUser);
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

}
