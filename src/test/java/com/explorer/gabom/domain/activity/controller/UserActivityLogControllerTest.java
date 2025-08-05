package com.explorer.gabom.domain.activity.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("UserActivityLogController - 통합 테스트")
class UserActivityLogControllerTest {

	private static final Long USER_ID = 1L;
	private static final Long TARGET_ID = 5L;
	private static final String IP_ADDRESS = "127.0.0.1";
	private static final String ACTIVITY_TYPE = "AUTH_LOGIN";
	private static final String DESCRIPTION = "사용자 로그인 성공";

	private static final LocalDateTime FROM = LocalDateTime.of(2025, 8, 1, 0, 0);
	private static final LocalDateTime TO = LocalDateTime.of(2025, 8, 2, 0, 0);

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserActivityLogService userActivityLogService;

	@MockBean
	private CustomUserDetailsService customUserDetailsService;

	private void setupAuthentication() {
		CustomUserDetails userDetails = CustomUserDetails.builder()
														 .userId(USER_ID)
														 .email("test@example.com")
														 .password("password")
														 .role(UserRole.USER)
														 .build();

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	@DisplayName("성공 - 내 활동 로그 조회")
	@WithMockUser
	void getMyLogs_success() throws Exception {
		setupAuthentication();
		List<UserActivityLogResponse> logList = List.of(createActivityLogResponse());
		PageResponse<UserActivityLogResponse> pageResponse = createPageResponse(logList);

		given(userActivityLogService.getMyLogs(eq(USER_ID), any(), any(), any()))
			.willReturn(pageResponse);

		mockMvc.perform(get("/api/activity-logs/me")
							.param("from", FROM.toString())
							.param("to", TO.toString())
							.accept(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("활동 로그가 성공적으로 조회되었습니다."))
			   .andExpect(jsonPath("$.data.content[0].id").value(10))
			   .andExpect(jsonPath("$.data.content[0].userId").value(USER_ID))
			   .andExpect(jsonPath("$.data.content[0].activityType").value(ACTIVITY_TYPE))
			   .andExpect(jsonPath("$.data.content[0].targetId").value(TARGET_ID))
			   .andExpect(jsonPath("$.data.content[0].description").value(DESCRIPTION))
			   .andExpect(jsonPath("$.data.content[0].ipAddress").value(IP_ADDRESS))
			   .andExpect(jsonPath("$.data.content[0].createdAt").exists())
			   .andDo(print());

		// ArgumentCaptor 로 검증해도 됨 (선택)
		ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
		then(userActivityLogService).should().getMyLogs(userIdCaptor.capture(), any(), any(), any());
		assertThat(userIdCaptor.getValue()).isEqualTo(USER_ID);
	}

	@Test
	@DisplayName("실패 - 인증 없이 요청 시 401 반환")
	void getMyLogs_unauthorized() throws Exception {
		mockMvc.perform(get("/api/activity-logs/me"))
			   .andExpect(status().isUnauthorized())
			   .andDo(print());
	}

	@Test
	@DisplayName("실패 - 서비스 내부 예외 발생 시 500 반환")
	@WithMockUser
	void getMyLogs_internalError() throws Exception {
		setupAuthentication();
		given(userActivityLogService.getMyLogs(anyLong(), any(), any(), any()))
			.willThrow(new RuntimeException("서버 내부 오류"));

		mockMvc.perform(get("/api/activity-logs/me"))
			   .andExpect(status().isInternalServerError())
			   .andDo(print());
	}

	private UserActivityLogResponse createActivityLogResponse() {
		return new UserActivityLogResponse(
			10L,
			USER_ID,
			ACTIVITY_TYPE,
			TARGET_ID,
			DESCRIPTION,
			IP_ADDRESS,
			LocalDateTime.now()
		);
	}

	private PageResponse<UserActivityLogResponse> createPageResponse(List<UserActivityLogResponse> content) {
		return PageResponse.<UserActivityLogResponse>builder()
						   .content(content)
						   .page(0)
						   .size(10)
						   .totalElements(content.size())
						   .totalPages(1)
						   .build();
	}
}

