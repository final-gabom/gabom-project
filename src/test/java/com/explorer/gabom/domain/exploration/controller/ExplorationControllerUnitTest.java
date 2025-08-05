package com.explorer.gabom.domain.exploration.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationCurrentResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationExtendTimeResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.exploration.service.ExplorationService;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

@ExtendWith(MockitoExtension.class)
class ExplorationControllerUnitTest {

	@InjectMocks
	private ExplorationController controller;

	@Mock
	private ExplorationService explorationService;

	private CustomUserDetails userDetails;

	@BeforeEach
	void setUp() {
		// 테스트용 User → CustomUserDetails 변환
		User user = User.builder()
						.id(1L)
						.email("test@example.com")
						.password("password")
						.nickname("user1")
						.userRole(UserRole.USER)
						.build();
		userDetails = CustomUserDetails.from(user);
	}

	@Test
	@DisplayName("탐험 시작 - 성공")
	void startExploration_success() {
		// given
		long placeId = 1L;
		ExplorationStartRequest req = new ExplorationStartRequest(37.0, 127.0);
		ExplorationStartResponse dto = ExplorationStartResponse.builder()
															   .explorationId(1L)
															   .rewardPoint(50)
															   .rewardExp(30)
															   .startAt(LocalDateTime.now())
															   .endAt(LocalDateTime.now().plusHours(3))
															   .build();
		given(explorationService.startExploration(userDetails.getUserId(), placeId, req))
			.willReturn(dto);

		// when
		ResponseEntity<ApiResponse<ExplorationStartResponse>> resp = controller.startExploration(placeId, req, userDetails);

		// then
		assertEquals(HttpStatus.OK, resp.getStatusCode());

		ApiResponse<ExplorationStartResponse> body = resp.getBody();
		assertNotNull(body);
		assertTrue(body.isSuccess());
		assertEquals("탐험이 시작되었습니다.", body.getMessage());
		assertEquals(dto, body.getData());
	}

	@Test
	@DisplayName("탐험 제한 시간 연장 - 성공")
	void extendExplorationTime_success() {
		// given
		long explorationId = 123L;
		ExplorationExtendTimeResponse dto =
			new ExplorationExtendTimeResponse(explorationId, LocalDateTime.now().plusHours(2));
		given(explorationService.extendExplorationTime(userDetails.getUserId(), explorationId))
			.willReturn(dto);

		// when
		ResponseEntity<ApiResponse<ExplorationExtendTimeResponse>> resp =
			controller.extendExplorationTime(userDetails, explorationId);

		// then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		ApiResponse<ExplorationExtendTimeResponse> body = resp.getBody();
		assertNotNull(body);
		assertTrue(body.isSuccess());
		assertEquals("탐험 제한 시간이 연장되었습니다.", body.getMessage());
		assertEquals(dto, body.getData());
	}

	@Test
	@DisplayName("탐험 중인 장소 조회 - 성공")
	void getCurrentExploration_success() {
		// given
		ExplorationCurrentResponse dto = ExplorationCurrentResponse.builder()
																   .explorationId(100L)
																   .placeId(5L)
																   .placeTitle("숨은 명소")
																   .startedAt(LocalDateTime.now().minusHours(1))
																   .deadline(LocalDateTime.now().plusHours(2))
																   .rewardPoint(300)
																   .build();
		given(explorationService.getCurrentExploration(userDetails.getUserId()))
			.willReturn(dto);

		// when
		ResponseEntity<ApiResponse<ExplorationCurrentResponse>> resp =
			controller.getCurrentExploration(userDetails);

		// then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		ApiResponse<ExplorationCurrentResponse> body = resp.getBody();
		assertNotNull(body);
		assertTrue(body.isSuccess());
		assertEquals("현재 탐험 중인 장소 조회에 성공했습니다.", body.getMessage());
		assertEquals(dto, body.getData());
	}
}
