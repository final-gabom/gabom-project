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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmailAuthControllerTest {

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
        EmailRequest request = new EmailRequest("test@example.com");

        doNothing().when(emailAuthService).sendAuthCode(ArgumentMatchers.any(EmailRequest.class));

        mockMvc.perform(post("/api/auth/email/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증 코드를 이메일로 전송했습니다."));
    }

    @Test
    void 이메일_인증코드_검증_성공() throws Exception {
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest("test@example.com", "123456");

        doNothing().when(emailAuthService).verifyAuthCode(ArgumentMatchers.any(EmailCodeVerifyRequest.class));

        mockMvc.perform(post("/api/auth/email/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("이메일 인증이 완료 되었습니다."));
    }

    @Test
    void 비밀번호_재설정_인증코드_전송_성공() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest("test@example.com");

        doNothing().when(passwordResetService).sendResetCode(ArgumentMatchers.any(PasswordResetRequest.class));

        mockMvc.perform(post("/api/auth/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 재설정 인증 코드를 이메일로 전송했습니다."));
    }

    @Test
    void 비밀번호_재설정_인증코드_검증_및_재설정_성공() throws Exception {
        PasswordResetVerifyRequest request = new PasswordResetVerifyRequest("test@example.com", "123456", "newPass123!");

        doNothing().when(passwordResetService).verifiedResetCode(ArgumentMatchers.any(PasswordResetVerifyRequest.class));

        mockMvc.perform(post("/api/auth/password-reset/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 재설정이 완료 되었습니다. "));
    }

}
