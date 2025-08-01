package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.explorer.gabom.global.validator.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailCodeStorageService emailCodeStorageService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private JwtProvider jwtProvider;


    @Test
    void 회원가입_성공() {
        // given
        SignupRequest request = new SignupRequest(
                "test@example.com",
                "nickname",
                "password123!",
                UserRole.USER
        );

        EmailCodeVerifyRequest verifyRequest = EmailCodeVerifyRequest.onlyEmail(request.getEmail());

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(emailCodeStorageService.isEmailVerified(any(EmailCodeVerifyRequest.class))).thenReturn(true);
        when(userRepository.existsByNickname(request.getNickname())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User fakeUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("nickname")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(fakeUser);

        // when
        SignupResponse response = authService.signup(request);

        // then
        assertEquals(1L, response.getId()); // id만 있으니 id 값으로 체크
    }

    @Test
    void 회원가입_실패_이메일중복() {
        // given
        SignupRequest request = new SignupRequest(
                "test@example.com", "nickname", "password123!", UserRole.USER
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.signup(request);
        });

        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void 회원가입_실패_이메일인증안됨() {
        // given
        SignupRequest request = new SignupRequest(
                "test@example.com", "nickname", "password123!", UserRole.USER
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(emailCodeStorageService.isEmailVerified(any(EmailCodeVerifyRequest.class))).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.signup(request);
        });

        assertEquals(ErrorCode.EMAIL_NOT_VERIFIED, exception.getErrorCode());
    }

    @Test
    void 회원가입_실패_닉네임중복() {
        // given
        SignupRequest request = new SignupRequest(
                "test@example.com", "nickname", "password123!", UserRole.USER
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(emailCodeStorageService.isEmailVerified(any(EmailCodeVerifyRequest.class))).thenReturn(true);
        when(userRepository.existsByNickname(request.getNickname())).thenReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.signup(request);
        });

        assertEquals(ErrorCode.NICKNAME_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void 로그인_성공() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "Password123!");

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded-password")
                .nickname("nickname")
                .userRole(UserRole.USER)
                .build();

        given(userRepository.findByEmailAndStatus(eq("test@example.com"), eq(UserStatus.ACTIVE)))
                .willReturn(Optional.of(user));

        // 비밀번호 검증은 내부에서 예외 던지지 않으면 성공으로 간주
        willDoNothing().given(passwordValidator).verifyMatch("Password123!", "encoded-password");

        given(jwtProvider.createAccessToken(1L, UserRole.USER)).willReturn("access-token");
        given(jwtProvider.createRefreshToken(1L, UserRole.USER)).willReturn("refresh-token");

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void 로그인_실패_유저없음() {
        // given
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123!");

        given(userRepository.findByEmailAndStatus(any(), any()))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> authService.login(request));
    }
}
