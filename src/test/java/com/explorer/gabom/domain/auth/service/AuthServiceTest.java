package com.explorer.gabom.domain.auth.service;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.explorer.gabom.global.validator.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    // 테스트용 상수
    private static final String EMAIL = "test@example.com";
    private static final String NICKNAME = "nickname";
    private static final String RAW_PASSWORD = "Password123!";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
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

    @BeforeEach
    void setup() {
        // 공통 mock 초기화는 필요 시 여기에
    }

    @DisplayName("회원가입 성공")
    @Test
    void signupSucces() {
        // given
        SignupRequest request = createSignupRequest();

        given(userRepository.existsByEmail(EMAIL)).willReturn(false);
        given(emailCodeStorageService.isEmailVerified(any(EmailCodeVerifyRequest.class))).willReturn(true);
        given(userRepository.existsByNickname(NICKNAME)).willReturn(false);
        given(passwordEncoder.encode(RAW_PASSWORD)).willReturn(ENCODED_PASSWORD);

        User savedUser = User.builder()
                .id(1L)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .nickname(NICKNAME)
                .userRole(UserRole.USER)
                .build();
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        UserSummaryDto response = authService.signup(request);

        // then
        assertThat(response.getId()).isEqualTo(1L);

        // ArgumentCaptor 사용해 실제 저장된 User 객체 확인
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        assertThat(captured.getEmail()).isEqualTo(EMAIL);
        assertThat(captured.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(captured.getNickname()).isEqualTo(NICKNAME);
        assertThat(captured.getUserRole()).isEqualTo(UserRole.USER);
    }

    @DisplayName("회원가입 실패 이메일 중복")
    @Test
    void signupFail_emailAlreadyExists() {
        // given
        SignupRequest request = createSignupRequest();
        given(userRepository.existsByEmail(EMAIL)).willReturn(true);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> authService.signup(request));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @DisplayName("회원가입 실패 이메일 미인증")
    @Test
    void signupFail_emailNotVerified() {
        // given
        SignupRequest request = createSignupRequest();
        given(userRepository.existsByEmail(EMAIL)).willReturn(false);
        given(emailCodeStorageService.isEmailVerified(any())).willReturn(false);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> authService.signup(request));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_NOT_VERIFIED);
    }

    @DisplayName("회원가입 실패 닉네임 중복")
    @Test
    void signupFail_nicknameAlreadyExists() {
        // given
        SignupRequest request = createSignupRequest();
        given(userRepository.existsByEmail(EMAIL)).willReturn(false);
        given(emailCodeStorageService.isEmailVerified(any())).willReturn(true);
        given(userRepository.existsByNickname(NICKNAME)).willReturn(true);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> authService.signup(request));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    @DisplayName("로그인 성공")
    @Test
    void LoginSuccess() {
        // given
        LoginRequest request = new LoginRequest(EMAIL, RAW_PASSWORD);
        User user = User.builder()
                .id(1L)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .nickname(NICKNAME)
                .userRole(UserRole.USER)
                .build();

        given(userRepository.findByEmailAndStatus(EMAIL, UserStatus.ACTIVE)).willReturn(Optional.of(user));
        willDoNothing().given(passwordValidator).verifyMatch(RAW_PASSWORD, ENCODED_PASSWORD);
        given(jwtProvider.createAccessToken(1L, UserRole.USER)).willReturn(ACCESS_TOKEN);
        given(jwtProvider.createRefreshToken(1L, UserRole.USER)).willReturn(REFRESH_TOKEN);

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

    @DisplayName("로그인 실패 유저없음")
    @Test
    void loginFail_userNotFound() {
        // given
        LoginRequest request = new LoginRequest("wrong@example.com", "pw");
        given(userRepository.findByEmailAndStatus(any(), any())).willReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> authService.login(request));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    // ---------- 팩토리 메소드 ----------
    private SignupRequest createSignupRequest() {
        return new SignupRequest(EMAIL, NICKNAME, RAW_PASSWORD, UserRole.USER);
    }
}
