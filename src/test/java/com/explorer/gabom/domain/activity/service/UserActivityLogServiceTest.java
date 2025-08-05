package com.explorer.gabom.domain.activity.service;

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
import org.mockito.Captor;
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
@DisplayName("UserActivityLogService - 단위 테스트")
class UserActivityLogServiceTest {

	private static final Long USER_ID = 1L;
	private static final Long TARGET_ID = 5L;
	private static final String IP_ADDRESS = "127.0.0.1";
	private static final String DESCRIPTION = "사용자 로그인 성공";
	private static final ActivityType ACTIVITY_TYPE = ActivityType.AUTH_LOGIN;
	private static final LocalDateTime FROM = LocalDateTime.of(2025, 8, 1, 0, 0);
	private static final LocalDateTime TO = LocalDateTime.of(2025, 8, 2, 0, 0);
	private static final Pageable PAGEABLE = PageRequest.of(0, 10);

	@InjectMocks
	private UserActivityLogService userActivityLogService;

	@Mock
	private UserActivityLogRepository userActivityLogRepository;

	@Captor private ArgumentCaptor<Long> userIdCaptor;
	@Captor private ArgumentCaptor<LocalDateTime> fromCaptor;
	@Captor private ArgumentCaptor<LocalDateTime> toCaptor;
	@Captor private ArgumentCaptor<Pageable> pageableCaptor;

	@Test
	@DisplayName("성공 - 내 활동 로그 조회 시 repository에 정확한 인자 전달")
	void getMyLogs_success() {
		// given
		UserActivityLog mockLog = createMockLog();
		Page<UserActivityLog> mockPage = new PageImpl<>(List.of(mockLog), PAGEABLE, 1);

		given(userActivityLogRepository.searchMyLogs(any(), any(), any(), any()))
			.willReturn(mockPage);

		// when
		PageResponse<UserActivityLogResponse> result = userActivityLogService.getMyLogs(USER_ID, FROM, TO, PAGEABLE);

		// then
		then(userActivityLogRepository).should()
									   .searchMyLogs(userIdCaptor.capture(), fromCaptor.capture(), toCaptor.capture(), pageableCaptor.capture());

		assertThat(result.getContent()).hasSize(1);
		UserActivityLogResponse logResponse = result.getContent().get(0);

		assertThat(logResponse.getId()).isEqualTo(10L);
		assertThat(logResponse.getActivityType()).isEqualTo(ACTIVITY_TYPE.name());

		assertThat(userIdCaptor.getValue()).isEqualTo(USER_ID);
		assertThat(fromCaptor.getValue()).isEqualTo(FROM);
		assertThat(toCaptor.getValue()).isEqualTo(TO);
		assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(PAGEABLE.getPageNumber());
		assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(PAGEABLE.getPageSize());
	}

	@Test
	@DisplayName("예외 발생 시 RuntimeException 반환")
	void getMyLogs_repositoryThrowsException_shouldThrow() {
		// given
		given(userActivityLogRepository.searchMyLogs(any(), any(), any(), any()))
			.willThrow(new RuntimeException("DB 에러"));

		// when & then
		assertThatThrownBy(() -> userActivityLogService.getMyLogs(USER_ID, FROM, TO, PAGEABLE))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("DB 에러");
	}

	private UserActivityLog createMockLog() {
		UserActivityLog log = new UserActivityLog(
			USER_ID,
			TARGET_ID,
			ACTIVITY_TYPE,
			DESCRIPTION,
			IP_ADDRESS
		);
		ReflectionTestUtils.setField(log, "id", 10L);
		return log;
	}
}

