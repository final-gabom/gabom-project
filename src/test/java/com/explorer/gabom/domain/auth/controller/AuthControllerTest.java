package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.service.AuthService;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    private static final String ACCESS_TOKEN = "mock-access-token";
    private static final String REFRESH_TOKEN = "mock-refresh-token";
    private static final String EMAIL = "test@example.com";
    private static final String NICKNAME = "nickname";
    private static final String PASSWORD = "Password123!";
    private static final Long USER_ID = 1L;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private org.springframework.data.mapping.context.MappingContext<?, ?> jpaMappingContext;

    @DisplayName("로그인 성공")
    @Test
    void signupSuccess() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "test@example.com",
                "nickname",
                "Password123!",
                UserRole.USER // null이 아닌 값으로 변경
        );
        UserSummaryDto userSummary = new UserSummaryDto(1L, "nickname", 0, "칭호");

        when(authService.signup(any(SignupRequest.class))).thenReturn(userSummary);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입을 성공했습니다."))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @DisplayName("잘못된 값일때 실패")
    @Test
    void signupFail_validationError() throws Exception {
        // role이 null → validation 실패 예상
        SignupRequest invalidRequest = new SignupRequest(
                "invalid-email",  // 이메일 형식 오류
                "a",              // 닉네임 길이 2 미만 오류
                "pwd",            // 비밀번호 패턴 오류
                null              // role null → NotNull validation 실패
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("로그인 성공")
    @Test
    void loginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

        // 로그인 성공 시 AuthService에서 반환할 DTO
        LoginResponse loginResponse = LoginResponse.toDto(ACCESS_TOKEN, REFRESH_TOKEN);

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // 200 OK 기대
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인을 성공했습니다."))
                .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN));
    }

    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    @Test
    void loginFail_invalidPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

        // 로그인 실패 시 AuthService에서 예외 발생 시뮬레이션
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new CustomException(ErrorCode.INCORRECT_PASSWORD));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())//
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 실패 - 잘못된 이메일 형식")
    @Test
    void signupFail_invalidEmailFormat() throws Exception {
        SignupRequest request = new SignupRequest(
                "invalid-email-format",  // 잘못된 이메일
                "validNickname",
                "Password123!",
                UserRole.USER
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 실패 - 비밀번호 형식 불일치")
    @Test
    void signupFail_invalidPassword() throws Exception {
        SignupRequest request = new SignupRequest(
                EMAIL,
                "validNickname",
                "abc",  // 너무 짧고 형식 불일치
                UserRole.USER
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 실패 - 닉네임 너무 짧음")
    @Test
    void signupFail_shortNickname() throws Exception {
        SignupRequest request = new SignupRequest(
                EMAIL,
                "a",  // 닉네임이 너무 짧음
                "Password123!",
                UserRole.USER
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @DisplayName("회원가입 실패 - 권한 누락(null)")
    @Test
    void signupFail_missingRole() throws Exception {
        SignupRequest request = new SignupRequest(
                EMAIL,
                "validNickname",
                "Password123!",
                null  // 권한 없음
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @TestConfiguration
    static class DisableJpaAuditingConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("test-user");
        }
    }
}
