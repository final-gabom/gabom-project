package com.explorer.gabom.domain.activity.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.explorer.gabom.domain.activity.dto.response.UserActivityLogResponse;
import com.explorer.gabom.domain.activity.entity.UserActivityLog;
import com.explorer.gabom.domain.activity.repository.UserActivityLogRepository;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.global.dto.PageResponse;

@ExtendWith(MockitoExtension.class)
class UserActivityLogServiceTest {

	@InjectMocks
	private UserActivityLogService userActivityLogService;

	@Mock
	private UserActivityLogRepository userActivityLogRepository;

	@Test
	void getMyLogs_success() {
		// given
		Long userId = 1L;
		LocalDateTime from = LocalDateTime.of(2025, 8, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2025, 8, 2, 0, 0);
		Pageable pageable = PageRequest.of(0, 10);

		UserActivityLog mockLog = new UserActivityLog(
			userId,  // userId
			5L,      // targetId
			ActivityType.AUTH_LOGIN,
			"사용자 로그인 성공",
			"127.0.0.1"
		);
		// ID 수동 설정
		ReflectionTestUtils.setField(mockLog, "id", 10L);

		Page<UserActivityLog> mockPage = new PageImpl<>(List.of(mockLog), pageable, 1);
		given(userActivityLogRepository.searchMyLogs(eq(userId), eq(from), eq(to), any(Pageable.class)))
			.willReturn(mockPage);

		// when
		PageResponse<UserActivityLogResponse> result = userActivityLogService.getMyLogs(userId, from, to, pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(10L);
		assertThat(result.getContent().get(0).getActivityType()).isEqualTo("AUTH_LOGIN");
	}


	@Test
	void getMyLogs_repositoryThrowsException_shouldThrow() {
		// given
		Long userId = 1L;
		LocalDateTime from = LocalDateTime.of(2025, 8, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2025, 8, 2, 0, 0);
		Pageable pageable = PageRequest.of(0, 10);

		given(userActivityLogRepository.searchMyLogs(any(), any(), any(), any()))
			.willThrow(new RuntimeException("DB 에러"));

		// when & then
		assertThatThrownBy(() -> userActivityLogService.getMyLogs(userId, from, to, pageable))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("DB 에러");
	}
}