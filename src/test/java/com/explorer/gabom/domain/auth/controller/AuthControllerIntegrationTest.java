package com.explorer.gabom.domain.auth.controller;

import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
import com.explorer.gabom.domain.auth.dto.request.SignupRequest;
import com.explorer.gabom.domain.auth.dto.request.LoginRequest;
import com.explorer.gabom.domain.auth.service.EmailCodeStorageService;
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
@DisplayName("AuthController 통합 테스트")
class AuthControllerIntegrationTest {

	private static final String SIGNUP_URL = "/api/auth/signup";
	private static final String TEST_SIGNUP_URL = "/api/auth/test/signup";
	private static final String EMAIL_REQUEST_URL = "/api/auth/email/request";
	private static final String EMAIL_VERIFY_URL = "/api/auth/email/verify";
	private static final String LOGIN_URL = "/api/auth/login";
	private static final String CHECK_NICKNAME_URL = "/api/auth/check-nickname";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EmailCodeStorageService emailCodeStorageService;

	@Test
	@DisplayName("이메일 인증 플로우 후 회원가입 성공")
	void signupAfterEmailVerification() throws Exception {
		// given: 인증 코드 전송 요청 DTO
		EmailRequest emailReq = new EmailRequest("flowuser@example.com");

		// when: 인증 코드 전송 API 호출
		mockMvc.perform(post(EMAIL_REQUEST_URL)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(emailReq)))
			   .andExpect(status().isOk());

		// and: 저장된 인증 코드 조회
		String code = emailCodeStorageService.getEmailAuthCode(emailReq);

		// when: 인증 코드 검증 API 호출
		EmailCodeVerifyRequest verifyReq = new EmailCodeVerifyRequest(emailReq.getEmail(), code);
		mockMvc.perform(post(EMAIL_VERIFY_URL)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(verifyReq)))
			   .andExpect(status().isOk());

		// given: 회원가입 요청 DTO
		SignupRequest signupReq = new SignupRequest(
			emailReq.getEmail(),
			"flowuser",
			"ValidP@ss1",
			UserRole.USER
		);

		// when & then: 회원가입 API 호출 및 검증
		mockMvc.perform(post(SIGNUP_URL)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(signupReq)))
			   .andExpect(status().isCreated())
			   .andExpect(jsonPath("$.message").value("회원가입을 성공했습니다."))
			   .andExpect(jsonPath("$.data.id").isNumber());
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
			   .andExpect(jsonPath("$.data.id").isNumber());
	}

	@Test
	@DisplayName("로그인 성공 (POST /api/auth/login)")
	void loginSuccess() throws Exception {
		// given
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
