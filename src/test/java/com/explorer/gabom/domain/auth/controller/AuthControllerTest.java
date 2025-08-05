package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.auth.service.AuthService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    // 테스트 상수
    private static final String EMAIL = "test@example.com";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "Password123!";
    private static final UserRole ROLE = UserRole.USER;
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtProvider jwtProvider;
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest(EMAIL, NICKNAME, PASSWORD, ROLE);
        loginRequest = new LoginRequest(EMAIL, PASSWORD);
    }

    @Test
    void 회원가입_성공() throws Exception {
        // given
        SignupResponse response = new SignupResponse(1L);
        when(authService.signup(any(SignupRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void 로그인_성공() throws Exception {
        // given
        LoginResponse response = LoginResponse.toDto(ACCESS_TOKEN, REFRESH_TOKEN);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN));
    }
}
