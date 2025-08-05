package com.explorer.gabom.domain.activity.controller;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.explorer.gabom.domain.activity.dto.response.UserActivityLogResponse;
import com.explorer.gabom.domain.activity.service.UserActivityLogService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import com.explorer.gabom.global.security.userdetails.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserActivityLogController - 단위 테스트")
class UserActivityLogControllerTest {

	private static final Long USER_ID = 1L;
	private static final Long TARGET_ID = 5L;
	private static final String IP_ADDRESS = "127.0.0.1";
	private static final String ACTIVITY_TYPE = "AUTH_LOGIN";
	private static final String DESCRIPTION = "사용자 로그인 성공";

	private static final LocalDateTime FROM = LocalDateTime.of(2025, 8, 1, 0, 0);
	private static final LocalDateTime TO = LocalDateTime.of(2025, 8, 2, 0, 0);

	@InjectMocks
	private UserActivityLogController userActivityLogController;

	@Mock
	private UserActivityLogService userActivityLogService;

	@Mock
	private CustomUserDetailsService customUserDetailsService; // 실제 사용 안 되면 제거 가능

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
	void getMyLogs_success() {
		// given
		setupAuthentication();

		// 수동으로 인증된 유저 객체 생성
		CustomUserDetails userDetails = CustomUserDetails.builder()
														 .userId(USER_ID)
														 .email("test@example.com")
														 .password("password")
														 .role(UserRole.USER)
														 .build();

		List<UserActivityLogResponse> logList = List.of(createActivityLogResponse());
		PageResponse<UserActivityLogResponse> pageResponse = createPageResponse(logList);

		given(userActivityLogService.getMyLogs(eq(USER_ID), any(), any(), any()))
			.willReturn(pageResponse);

		// when
		ResponseEntity<ApiResponse<PageResponse<UserActivityLogResponse>>> result =
			userActivityLogController.getMyLogs(userDetails, FROM, TO, null);

		// then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		ApiResponse<PageResponse<UserActivityLogResponse>> body = result.getBody();
		assertThat(body).isNotNull();
		assertThat(body.isSuccess()).isTrue();
		assertThat(body.getMessage()).isEqualTo("활동 로그가 성공적으로 조회되었습니다.");
		assertThat(body.getData().getContent()).hasSize(1);
		assertThat(body.getData().getContent().get(0).getId()).isEqualTo(10L);
		assertThat(body.getData().getContent().get(0).getUserId()).isEqualTo(USER_ID);
		assertThat(body.getData().getContent().get(0).getActivityType()).isEqualTo(ACTIVITY_TYPE);
		assertThat(body.getData().getContent().get(0).getTargetId()).isEqualTo(TARGET_ID);
		assertThat(body.getData().getContent().get(0).getDescription()).isEqualTo(DESCRIPTION);
		assertThat(body.getData().getContent().get(0).getIpAddress()).isEqualTo(IP_ADDRESS);
		assertThat(body.getData().getContent().get(0).getCreatedAt()).isNotNull();

		// verify
		ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
		then(userActivityLogService).should().getMyLogs(userIdCaptor.capture(), any(), any(), any());
		assertThat(userIdCaptor.getValue()).isEqualTo(USER_ID);
	}

	@Test
	@DisplayName("실패 - 인증 없이 요청 시 401 예외")
	void getMyLogs_unauthorized() {
		// when & then
		SecurityContextHolder.clearContext(); // 인증 제거
		assertThatThrownBy(() -> userActivityLogController.getMyLogs(null, FROM, TO, null))
			.isInstanceOf(CustomException.class)
			.extracting(e -> ((CustomException) e).getErrorCode())
			.isEqualTo(ErrorCode.UNAUTHORIZED);
	}

	@Test
	@DisplayName("실패 - 서비스 내부 예외 발생 시 RuntimeException 발생")
	void getMyLogs_internalError() {
		// given
		setupAuthentication();
		CustomUserDetails userDetails = CustomUserDetails.builder()
														 .userId(USER_ID)
														 .email("test@example.com")
														 .password("password")
														 .role(UserRole.USER)
														 .build();
		given(userActivityLogService.getMyLogs(anyLong(), any(), any(), any()))
			.willThrow(new RuntimeException("서버 내부 오류"));

		// when & then
		assertThatThrownBy(() -> userActivityLogController.getMyLogs(userDetails, FROM, TO, null))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("서버 내부 오류");
	}

	// Stub 응답 객체 생성
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


