package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.response.LoginResponse;
import com.explorer.gabom.domain.auth.dto.response.SignupResponse;
import com.explorer.gabom.domain.auth.service.AuthService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    void 회원가입_성공() throws Exception {
        // given
        SignupRequest request = new SignupRequest(
                "test@example.com",
                "nickname",
                "Password123!",
                UserRole.USER
        );
        SignupResponse response = new SignupResponse(1L);

        when(authService.signup(any(SignupRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }
    @Test
    void 로그인_성공() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "Password123!");
        LoginResponse response = LoginResponse.toDto("access-token", "refresh-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }
}
