package com.explorer.gabom.domain.exploration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationCurrentResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationExtendTimeResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.exploration.entity.Exploration;
import com.explorer.gabom.domain.exploration.repository.ExplorationRepository;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExplorationService 단위 테스트")
public class ExplorationServiceTest {

	@Mock private UserRepository userRepository;
	@Mock private PlaceRepository placeRepository;
	@Mock private ExplorationRepository explorationRepository;

	@InjectMocks private ExplorationService explorationService;

	private User mockUser;
	private Place mockPlace;
	private Exploration exploration;

	@BeforeEach
	void setUp() {
		mockUser = User.builder()
					   .id(1L)
					   .build();

		mockPlace = Place.builder()
						 .id(1L)
						 .lat(37.123456)
						 .lng(127.123456)
						 .build();

		exploration = Exploration.builder()
								 .id(1L)
								 .user(mockUser)
								 .endAt(LocalDateTime.now().plusMinutes(30))
								 .build();
	}


	// 탐험 시작
	@Test
	@DisplayName("탐험 시작 - 성공")
	void startExploration_success() {
		Long userId = 1L;
		Long placeId = 1L;
		ExplorationStartRequest request = new ExplorationStartRequest(37.0, 127.0);

		given(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.willReturn(false);

		given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
		given(placeRepository.findById(placeId)).willReturn(Optional.of(mockPlace));

		given(explorationRepository.save(any(Exploration.class))).willAnswer(invocation -> {
			Exploration arg = invocation.getArgument(0);
			return Exploration.builder()
							  .user(arg.getUser())
							  .place(arg.getPlace())
							  .startAt(arg.getStartAt())
							  .endAt(arg.getEndAt())
							  .rewardPoint(arg.getRewardPoint())
							  .rewardExp(arg.getRewardExp())
							  .build();
		});

		ExplorationStartResponse response = explorationService.startExploration(userId, placeId, request);

		assertNotNull(response);
		assertTrue(response.getRewardExp() > 0);
		assertTrue(response.getRewardPoint() > 0);
		assertNotNull(response.getStartAt());
		assertNotNull(response.getEndAt());

		then(explorationRepository).should().existsByUserIdAndPlaceIdAndEndAtAfter(eq(userId), eq(placeId), any(LocalDateTime.class));
		then(userRepository).should().findById(userId);
		then(placeRepository).should().findById(placeId);
		then(explorationRepository).should().save(any(Exploration.class));
	}

	@Test
	@DisplayName("탐험 시작 - 이미 진행 중인 탐험 있음 예외 발생")
	void startExploration_alreadyStartedException() {
		given(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.willReturn(true);

		CustomException exception = assertThrows(CustomException.class, () -> {
			explorationService.startExploration(1L, 1L, new ExplorationStartRequest(37.0, 127.0));
		});

		assertEquals(ErrorCode.ALREADY_STARTED_EXPLORATION, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 시작 - 유저 없음 예외 발생")
	void startExploration_userNotFoundException() {
		given(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.willReturn(false);

		given(userRepository.findById(anyLong())).willReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () -> {
			explorationService.startExploration(1L, 1L, new ExplorationStartRequest(37.0, 127.0));
		});

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 시작 - 장소 없음 예외 발생")
	void startExploration_placeNotFoundException() {
		given(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.willReturn(false);

		given(userRepository.findById(anyLong())).willReturn(Optional.of(mockUser));
		given(placeRepository.findById(anyLong())).willReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () -> {
			explorationService.startExploration(1L, 1L, new ExplorationStartRequest(37.0, 127.0));
		});

		assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getErrorCode());
	}


	// 탐험 제한 시간 연장
	@Test
	@DisplayName("탐험 제한 시간 연장 - 성공")
	void extendExplorationTime_success() {
		given(explorationRepository.findById(1L)).willReturn(Optional.of(exploration));

		ExplorationExtendTimeResponse response = explorationService.extendExplorationTime(1L, 1L);

		assertEquals(1L, response.getExplorationId());
		assertTrue(response.getNewDeadline().isAfter(LocalDateTime.now()));

		then(explorationRepository).should().findById(1L);
	}

	@Test
	@DisplayName("탐험 제한 시간 연장 - 탐험 없음 예외 발생")
	void extendExplorationTime_explorationNotFoundException() {
		given(explorationRepository.findById(1L)).willReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
												 () -> explorationService.extendExplorationTime(1L, 1L));

		assertEquals(ErrorCode.EXPLORATION_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 제한 시간 연장 - 권한 없음 예외 발생")
	void extendExplorationTime_noPermissionException() {
		User otherUser = User.builder().id(2L).build();
		exploration = Exploration.builder()
								 .id(1L)
								 .user(otherUser)
								 .endAt(LocalDateTime.now().plusMinutes(30))
								 .build();

		given(explorationRepository.findById(1L)).willReturn(Optional.of(exploration));

		CustomException exception = assertThrows(CustomException.class,
												 () -> explorationService.extendExplorationTime(1L, 1L));

		assertEquals(ErrorCode.EXPLORATION_NO_PERMISSION, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 제한 시간 연장 - 이미 종료된 탐험 예외 발생")
	void extendExplorationTime_alreadyEndedException() {
		exploration = Exploration.builder()
								 .id(1L)
								 .user(mockUser)
								 .endAt(LocalDateTime.now().minusMinutes(1))
								 .build();

		given(explorationRepository.findById(1L)).willReturn(Optional.of(exploration));

		CustomException exception = assertThrows(CustomException.class,
												 () -> explorationService.extendExplorationTime(1L, 1L));

		assertEquals(ErrorCode.EXPLORATION_ALREADY_ENDED, exception.getErrorCode());
	}


	// 탐험 중인 장소 조회
	@Test
	@DisplayName("탐험 중인 장소 조회 - 성공")
	void getCurrentExploration_success() {
		User user = User.builder().id(1L).build();
		Place place = Place.builder().id(1L).title("멋진 장소").build();

		Exploration activeExploration = Exploration.builder()
												   .id(100L)
												   .user(user)
												   .place(place)
												   .startAt(LocalDateTime.now().minusHours(1))
												   .endAt(LocalDateTime.now().plusHours(2))
												   .rewardPoint(300)
												   .build();

		given(explorationRepository.findTopByUserIdAndEndAtAfterOrderByEndAtAsc(eq(1L), any(LocalDateTime.class)))
			.willReturn(Optional.of(activeExploration));

		ExplorationCurrentResponse response = explorationService.getCurrentExploration(1L);

		assertNotNull(response);
		assertEquals(100L, response.getExplorationId());
		assertEquals(1L, response.getPlaceId());
		assertEquals("멋진 장소", response.getPlaceTitle());
		assertTrue(response.getDeadline().isAfter(LocalDateTime.now()));
		assertEquals(300, response.getRewardPoint());

		then(explorationRepository).should().findTopByUserIdAndEndAtAfterOrderByEndAtAsc(eq(1L), any(LocalDateTime.class));
	}

	@Test
	@DisplayName("탐험 중인 장소 조회 - 진행 중인 탐험 없음 예외 발생")
	void getCurrentExploration_noCurrentExplorationException() {
		given(explorationRepository.findTopByUserIdAndEndAtAfterOrderByEndAtAsc(eq(1L), any(LocalDateTime.class)))
			.willReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
												 () -> explorationService.getCurrentExploration(1L));

		assertEquals(ErrorCode.NO_ACTIVE_EXPLORATION, exception.getErrorCode());
	}
}