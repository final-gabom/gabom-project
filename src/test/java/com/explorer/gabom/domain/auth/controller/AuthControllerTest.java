package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.auth.service.AuthService;
import com.explorer.gabom.global.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private static final String EMAIL = "test@example.com";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "Password123!";
    private static final SignupRequest signupRequest = new SignupRequest(EMAIL, NICKNAME, PASSWORD, null); // ROLE 없이 생성 가능하면 null
    private static final LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 회원가입_성공() {
        SignupResponse signupResponse = new SignupResponse(1L);
        when(authService.signup(any(SignupRequest.class))).thenReturn(signupResponse);

        ResponseEntity<ApiResponse<SignupResponse>> responseEntity = authController.signup(signupRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().isSuccess()).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("회원가입을 성공했습니다.");
        assertThat(responseEntity.getBody().getData().getId()).isEqualTo(1L);
    }

    @Test
    void 회원가입_실패_예외() {
        when(authService.signup(any(SignupRequest.class)))
                .thenThrow(new IllegalArgumentException("이미 존재하는 이메일입니다."));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> authController.signup(signupRequest));

        assertThat(thrown.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }

    @Test
    void 로그인_성공() {
        LoginResponse loginResponse = LoginResponse.toDto(ACCESS_TOKEN, REFRESH_TOKEN);
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<ApiResponse<LoginResponse>> responseEntity = authController.login(loginRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().isSuccess()).isTrue();
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("로그인을 성공했습니다.");
        assertThat(responseEntity.getBody().getData().getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(responseEntity.getBody().getData().getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

    @Test
    void 로그인_실패_예외() {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("비밀번호가 일치하지 않습니다."));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> authController.login(loginRequest));

        assertThat(thrown.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
    }
}