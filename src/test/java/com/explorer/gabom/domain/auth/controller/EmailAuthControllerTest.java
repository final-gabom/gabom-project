package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;
import com.explorer.gabom.domain.auth.service.EmailAuthService;
import com.explorer.gabom.domain.auth.service.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailAuthControllerTest {

    // 공통 테스트 상수
    private static final String EMAIL = "test@example.com";
    private static final String CODE = "123456";
    private static final String PASSWORD = "newPass123!";
    private MockMvc mockMvc;
    @InjectMocks
    private EmailAuthController emailAuthController;
    @Mock
    private EmailAuthService emailAuthService;
    @Mock
    private PasswordResetService passwordResetService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailAuthController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void 이메일_인증코드_전송_성공() throws Exception {
        // given
        EmailRequest request = new EmailRequest(EMAIL);
        doNothing().when(emailAuthService).sendAuthCode(any(EmailRequest.class));

        // when & then
        mockMvc.perform(post("/api/auth/email/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인증 코드를 이메일로 전송했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(emailAuthService).sendAuthCode(any(EmailRequest.class));
    }

    @Test
    void 이메일_인증코드_검증_성공() throws Exception {
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest(EMAIL, CODE);
        doNothing().when(emailAuthService).verifyAuthCode(any(EmailCodeVerifyRequest.class));

        mockMvc.perform(post("/api/auth/email/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("이메일 인증이 완료 되었습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(emailAuthService).verifyAuthCode(any(EmailCodeVerifyRequest.class));
    }

    @Test
    void 비밀번호_재설정_인증코드_전송_성공() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest(EMAIL);
        doNothing().when(passwordResetService).sendResetCode(any(PasswordResetRequest.class));

        mockMvc.perform(post("/api/auth/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("비밀번호 재설정 인증 코드를 이메일로 전송했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(passwordResetService).sendResetCode(any(PasswordResetRequest.class));
    }

    @Test
    void 비밀번호_재설정_검증_및_비밀번호변경_성공() throws Exception {
        PasswordResetVerifyRequest request = new PasswordResetVerifyRequest(EMAIL, CODE, PASSWORD);
        doNothing().when(passwordResetService).verifiedResetCode(any(PasswordResetVerifyRequest.class));

        mockMvc.perform(post("/api/auth/password-reset/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("비밀번호 재설정이 완료 되었습니다. "))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(passwordResetService).verifiedResetCode(any(PasswordResetVerifyRequest.class));
    }
}
