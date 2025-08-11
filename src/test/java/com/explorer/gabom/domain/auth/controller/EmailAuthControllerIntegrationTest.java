// package com.explorer.gabom.domain.auth.controller;
//
// import com.explorer.gabom.domain.auth.dto.request.EmailCodeVerifyRequest;
// import com.explorer.gabom.domain.auth.dto.request.EmailRequest;
// import com.explorer.gabom.domain.auth.dto.request.PasswordResetRequest;
// import com.explorer.gabom.domain.auth.dto.request.PasswordResetVerifyRequest;
// import com.explorer.gabom.domain.auth.service.EmailCodeStorageService;
// import com.explorer.gabom.domain.user.entity.User;
// import com.explorer.gabom.domain.user.repository.UserRepository;
// import com.explorer.gabom.domain.user.type.UserRole;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.redis.core.StringRedisTemplate;
// import org.springframework.http.MediaType;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @Transactional
// @DisplayName("EmailAuth 통합 테스트")
// public class EmailAuthControllerIntegrationTest {
//
// 	@Autowired private MockMvc mockMvc;
// 	@Autowired private ObjectMapper objectMapper;
// 	@Autowired private EmailCodeStorageService emailCodeStorageService;
// 	@Autowired private UserRepository userRepository;
// 	@Autowired private PasswordEncoder passwordEncoder;
// 	@Autowired private StringRedisTemplate redisTemplate;
//
// 	private static final String EMAIL_REQUEST_URL          = "/api/auth/email/request";
// 	private static final String EMAIL_VERIFY_URL           = "/api/auth/email/verify";
// 	private static final String PASSWORD_RESET_REQUEST_URL = "/api/auth/password-reset/request";
// 	private static final String PASSWORD_RESET_VERIFY_URL  = "/api/auth/password-reset/verify";
//
// 	@BeforeEach
// 	void setUp() {
// 		// Redis와 DB 초기화
// 		redisTemplate.getConnectionFactory().getConnection().flushDb();
// 		userRepository.deleteAll();
// 	}
//
// 	@Test
// 	@DisplayName("이메일 인증 코드 요청 성공")
// 	void requestEmail_success() throws Exception {
// 		// given
// 		EmailRequest request = new EmailRequest("test@example.com");
// 		// when & then
// 		mockMvc.perform(post(EMAIL_REQUEST_URL)
// 							.contentType(MediaType.APPLICATION_JSON)
// 							.content(objectMapper.writeValueAsString(request)))
// 			   .andExpect(status().isOk())
// 			   .andExpect(jsonPath("$.message").value("인증 코드를 이메일로 전송했습니다."));
// 	}
//
// 	@Test
// 	@DisplayName("이메일 인증 코드 검증 성공")
// 	void verifyEmail_success() throws Exception {
// 		// given: 코드 발송
// 		EmailRequest emailReq = new EmailRequest("test@example.com");
// 		mockMvc.perform(post(EMAIL_REQUEST_URL)
// 							.contentType(MediaType.APPLICATION_JSON)
// 							.content(objectMapper.writeValueAsString(emailReq)))
// 			   .andExpect(status().isOk());
// 		// when: 코드 검증
// 		String code = emailCodeStorageService.getEmailAuthCode(emailReq);
// 		EmailCodeVerifyRequest verifyReq = new EmailCodeVerifyRequest(emailReq.getEmail(), code);
// 		mockMvc.perform(post(EMAIL_VERIFY_URL)
// 							.contentType(MediaType.APPLICATION_JSON)
// 							.content(objectMapper.writeValueAsString(verifyReq)))
// 			   // then
// 			   .andExpect(status().isOk())
// 			   .andExpect(jsonPath("$.message").value("이메일 인증이 완료 되었습니다."));
// 	}
//
// 	@Test
// 	@DisplayName("비밀번호 재설정 인증 코드 요청 성공")
// 	void passwordResetRequest_success() throws Exception {
// 		// given: 가입 사용자
// 		User user = User.builder()
// 						.email("user@example.com")
// 						.password(passwordEncoder.encode("InitialP@ss1"))
// 						.nickname("user1")
// 						.userRole(UserRole.USER)
// 						.build();
// 		userRepository.save(user);
// 		PasswordResetRequest request = new PasswordResetRequest("user@example.com");
// 		// when & then
// 		mockMvc.perform(post(PASSWORD_RESET_REQUEST_URL)
// 							.contentType(MediaType.APPLICATION_JSON)
// 							.content(objectMapper.writeValueAsString(request)))
// 			   .andExpect(status().isOk())
// 			   .andExpect(jsonPath("$.message").value("비밀번호 재설정 인증 코드를 이메일로 전송했습니다."));
// 	}
//
// 	@Test
// 	@DisplayName("비밀번호 재설정 인증 코드 검증 및 비밀번호 변경 성공")
// 	void passwordResetVerify_success() throws Exception {
// 		// given: 가입 사용자
// 		User user = User.builder()
// 						.email("reset@example.com")
// 						.password(passwordEncoder.encode("InitP@ss1"))
// 						.nickname("resetuser")
// 						.userRole(UserRole.USER)
// 						.build();
// 		userRepository.save(user);
// 		// when: 인증 코드 발송
// 		PasswordResetRequest request = new PasswordResetRequest("reset@example.com");
// 		mockMvc.perform(post(PASSWORD_RESET_REQUEST_URL)
// 							.contentType(MediaType.APPLICATION_JSON)
// 							.content(objectMapper.writeValueAsString(request)))
// 			   .andExpect(status().isOk());
// 		// and: 코드 검증 및 비밀번호 변경
// 		String code = emailCodeStorageService.getPasswordResetCode(user.getEmail());
// 		PasswordResetVerifyRequest verifyReq = new PasswordResetVerifyRequest(
// 			"reset@example.com", code, "NewP@ss2"
// 		);
// 		mockMvc.perform(post(PASSWORD_RESET_VERIFY_URL)
// 							.contentType(MediaType.APPLICATION_JSON)
// 							.content(objectMapper.writeValueAsString(verifyReq)))
// 			   // then
// 			   .andExpect(status().isOk())
// 			   .andExpect(jsonPath("$.message").value("비밀번호 재설정이 완료 되었습니다."));
// 	}
// }
