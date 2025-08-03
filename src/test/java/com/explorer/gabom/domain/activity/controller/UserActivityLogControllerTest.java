package com.explorer.gabom.domain.activity.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.explorer.gabom.domain.activity.dto.response.UserActivityLogResponse;
import com.explorer.gabom.domain.activity.service.UserActivityLogService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import com.explorer.gabom.global.security.userdetails.CustomUserDetailsService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserActivityLogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserActivityLogService userActivityLogService;

	@MockBean
	private CustomUserDetailsService customUserDetailsService; // 인증 필요 시

	@BeforeEach
	void setupSecurityContext() {
		// CustomUserDetails 직접 생성
		CustomUserDetails userDetails = CustomUserDetails.builder()
														 .userId(1L)
														 .email("test@example.com")
														 .password("password")
														 .role(UserRole.USER)
														 .user(null) // 필요하면 실제 User 엔티티 넣어도 됨
														 .build();

		// Authentication 객체 생성 후 SecurityContext에 세팅
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	// 테스트: 성공 케이스
	@Test
	@WithMockUser
	void getMyLogs_success() throws Exception {
		// given
		Long userId = 1L;
		LocalDateTime from = LocalDateTime.of(2025, 8, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2025, 8, 2, 0, 0);

		List<UserActivityLogResponse> logList = List.of(
			new UserActivityLogResponse(
				10L,
				userId,
				"AUTH_LOGIN",
				5L,
				"사용자 로그인 성공",
				"127.0.0.1",
				LocalDateTime.now())
		);

		PageResponse<UserActivityLogResponse> pageResponse = PageResponse.<UserActivityLogResponse>builder()
			.content(logList).page(0).size(10).totalElements(1).totalPages(1).build();

		given(userActivityLogService.getMyLogs(anyLong(), any(), any(), any()))
			.willReturn(pageResponse);



		// when & then
		mockMvc.perform(get("/api/activity-logs/me")
							.param("from", LocalDateTime.now().minusDays(1).toString())
							.param("to", LocalDateTime.now().plusDays(1).toString())
							.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("활동 로그가 성공적으로 조회되었습니다."))
			   .andExpect(jsonPath("$.data.content[0].id").value(10))
			   .andExpect(jsonPath("$.data.content[0].userId").value(userId))
			   .andExpect(jsonPath("$.data.content[0].activityType").value("AUTH_LOGIN"))
			   .andExpect(jsonPath("$.data.content[0].targetId").value(5))
			   .andExpect(jsonPath("$.data.content[0].description").value("사용자 로그인 성공"))
			   .andExpect(jsonPath("$.data.content[0].ipAddress").value("127.0.0.1"))
			   .andExpect(jsonPath("$.data.content[0].createdAt").exists())
			   .andDo(print());
	}

	// 테스트: 인증 실패 (401)
	@Test
	void getMyLogs_unauthorized() throws Exception {
		mockMvc.perform(get("/api/activity-logs/me"))
			   .andExpect(status().isUnauthorized())
			   .andDo(print());
	}

	// 테스트: 서비스 레이어에서 예외 발생 (500 or 커스텀 예외에 따라)
	@Test
	@WithMockUser
	void getMyLogs_internalError() throws Exception {
		given(userActivityLogService.getMyLogs(anyLong(), any(), any(), any()))
			.willThrow(new RuntimeException("서버 내부 오류"));

		mockMvc.perform(get("/api/activity-logs/me"))
			   .andExpect(status().isInternalServerError())
			   .andDo(print());
	}
}