package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;

import com.explorer.gabom.domain.auth.service.AuthService;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private static final String EMAIL = "test@example.com";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "Password123!";
    private static final Long USER_ID = 1L;
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        signupRequest = createSignupRequest();
        loginRequest = createLoginRequest();
    }

    @Test
    void 회원가입_성공() {
        // given
        SignupResponse signupResponse = new SignupResponse(USER_ID);
        when(authService.signup(any(SignupRequest.class))).thenReturn(signupResponse);

        // when
        ResponseEntity<ApiResponse<SignupResponse>> responseEntity = authController.signup(signupRequest);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().isSuccess()).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("회원가입을 성공했습니다.");
        assertThat(responseEntity.getBody().getData().getId()).isEqualTo(USER_ID);

        // ArgumentCaptor 사용
        ArgumentCaptor<SignupRequest> captor = ArgumentCaptor.forClass(SignupRequest.class);
        verify(authService).signup(captor.capture());
        SignupRequest captured = captor.getValue();
        assertThat(captured.getEmail()).isEqualTo(EMAIL);
        assertThat(captured.getNickname()).isEqualTo(NICKNAME);
    }

    @Test
    void 회원가입_실패_예외() {
        // given
        when(authService.signup(any(SignupRequest.class)))
                .thenThrow(new IllegalArgumentException("이미 존재하는 이메일입니다."));

        // when & then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> authController.signup(signupRequest));

        assertThat(thrown.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }

    @Test
    void 로그인_성공() {
        // given
        LoginResponse loginResponse = LoginResponse.toDto(ACCESS_TOKEN, REFRESH_TOKEN);
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // when
        ResponseEntity<ApiResponse<LoginResponse>> responseEntity = authController.login(loginRequest);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().isSuccess()).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("로그인을 성공했습니다.");
        assertThat(responseEntity.getBody().getData().getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(responseEntity.getBody().getData().getRefreshToken()).isEqualTo(REFRESH_TOKEN);

        // ArgumentCaptor 사용
        ArgumentCaptor<LoginRequest> captor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(authService).login(captor.capture());
        LoginRequest captured = captor.getValue();
        assertThat(captured.getEmail()).isEqualTo(EMAIL);
        assertThat(captured.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void 로그인_실패_예외() {
        // given
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("비밀번호가 일치하지 않습니다."));

        // when & then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> authController.login(loginRequest));

        assertThat(thrown.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
    }

    private SignupRequest createSignupRequest() {
        return new SignupRequest(EMAIL, NICKNAME, PASSWORD, null); // ROLE=null로 전달
    }

    private LoginRequest createLoginRequest() {
        return new LoginRequest(EMAIL, PASSWORD);
    }
}
