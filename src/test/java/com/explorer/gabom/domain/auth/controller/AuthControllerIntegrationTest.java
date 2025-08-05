package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.user.type.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerIntegrationTest {

	private static final String SIGNUP_URL = "/api/auth/signup";
	private static final String TEST_SIGNUP_URL = "/api/auth/test/signup";
	private static final String LOGIN_URL = "/api/auth/login";
	private static final String CHECK_NICKNAME_URL = "/api/auth/check-nickname";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원가입 성공 (POST /api/auth/signup)")
	void signupSuccess() throws Exception {
		// given
		SignupRequest req = new SignupRequest(
			"newuser@example.com",
			"newbie",
			"ValidP@ss1",
			UserRole.USER
		);
		// when
		mockMvc.perform(post(SIGNUP_URL)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
			   // then
			   .andExpect(status().isCreated())
			   .andExpect(jsonPath("$.message").value("회원가입을 성공했습니다."))
			   .andExpect(jsonPath("$.data.userId").isNumber())
			   .andExpect(jsonPath("$.data.email").value("newuser@example.com"))
			   .andExpect(jsonPath("$.data.nickname").value("newbie"))
			   .andExpect(jsonPath("$.data.role").value("USER"));
	}

	@Test
	@DisplayName("테스트용 회원가입 성공 (POST /api/auth/test/signup)")
	void testSignupSuccess() throws Exception {
		// given
		SignupRequest req = new SignupRequest(
			"testuser@example.com",
			"testuser",
			"TestP@ss123",
			UserRole.ADMIN
		);
		// when
		mockMvc.perform(post(TEST_SIGNUP_URL)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(req)))
			   // then
			   .andExpect(status().isCreated())
			   .andExpect(jsonPath("$.message").value("회원가입을 성공했습니다."))
			   .andExpect(jsonPath("$.data.userId").isNumber())
			   .andExpect(jsonPath("$.data.email").value("testuser@example.com"))
			   .andExpect(jsonPath("$.data.nickname").value("testuser"))
			   .andExpect(jsonPath("$.data.role").value("ADMIN"));
	}

	@Test
	@DisplayName("로그인 성공 (POST /api/auth/login)")
	void loginSuccess() throws Exception {
		// given: 미리 테스트용 회원가입 수행
		SignupRequest signupReq = new SignupRequest(
			"loginuser@example.com",
			"loginuser",
			"LoginP@ss1",
			UserRole.USER
		);
		mockMvc.perform(post(TEST_SIGNUP_URL)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(signupReq)))
			   .andExpect(status().isCreated());

		// given
		LoginRequest loginReq = new LoginRequest(
			signupReq.getEmail(),
			signupReq.getPassword()
		);
		// when
		mockMvc.perform(post(LOGIN_URL)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(loginReq)))
			   // then
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.message").value("로그인을 성공했습니다."))
			   .andExpect(jsonPath("$.data.accessToken").isString())
			   .andExpect(jsonPath("$.data.refreshToken").isString());
	}

	@Test
	@DisplayName("닉네임 중복 확인 성공 (GET /api/auth/check-nickname)")
	void checkNicknameSuccess() throws Exception {
		// when
		mockMvc.perform(get(CHECK_NICKNAME_URL)
							.param("nickname", "uniqueNickname"))
			   // then
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.message").value("닉네임 중복확인을 완료하였습니다."))
			   .andExpect(jsonPath("$.data.available").value(true));
	}
}
