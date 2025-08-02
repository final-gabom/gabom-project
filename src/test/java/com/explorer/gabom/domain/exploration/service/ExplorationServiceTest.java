package com.explorer.gabom.domain.exploration.service;

import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class ExplorationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private ExplorationRepository explorationRepository;

	@InjectMocks
	private ExplorationService explorationService;

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

		exploration = new Exploration();
		exploration.setId(1L);
		exploration.setUser(mockUser);
		exploration.setEndAt(LocalDateTime.now().plusMinutes(30));
	}

	// 탐험 시작
	@Test
	@DisplayName("탐험 시작 - 성공")
	void startExploration_success() throws Exception {
		// given
		Long userId = 1L;
		Long placeId = 1L;

		ExplorationStartRequest request = new ExplorationStartRequest();

		// reflection으로 lat, lng 세팅
		Field latField = ExplorationStartRequest.class.getDeclaredField("lat");
		latField.setAccessible(true);
		latField.set(request, 37.123456);

		Field lngField = ExplorationStartRequest.class.getDeclaredField("lng");
		lngField.setAccessible(true);
		lngField.set(request, 127.123456);

		// 이미 시작한 탐험 없다고 모킹
		when(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.thenReturn(false);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(placeRepository.findById(placeId)).thenReturn(Optional.of(mockPlace));

		// save 할 때 탐험 엔티티를 반환하도록 모킹
		when(explorationRepository.save(any(Exploration.class))).thenAnswer(invocation -> {
			Exploration exploration = invocation.getArgument(0);
			// id는 보통 DB에서 생성하므로 테스트용으로 임의 지정
			Field idField = Exploration.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(exploration, 1L);
			return exploration;
		});

		// when
		ExplorationStartResponse response = explorationService.startExploration(userId, placeId, request);

		// then
		assertNotNull(response);
		assertEquals(1L, response.getExplorationId());
		assertTrue(response.getRewardExp() > 0);
		assertTrue(response.getRewardPoint() > 0);
		assertNotNull(response.getStartAt());
		assertNotNull(response.getEndAt());

		// verify 메서드 호출
		verify(explorationRepository).existsByUserIdAndPlaceIdAndEndAtAfter(eq(userId), eq(placeId), any(LocalDateTime.class));
		verify(userRepository).findById(userId);
		verify(placeRepository).findById(placeId);
		verify(explorationRepository).save(any(Exploration.class));
	}

	@Test
	@DisplayName("탐험 시작 - 이미 진행 중인 탐험 있음 예외 발생")
	void startExploration_alreadyStartedExploration_throwException() {
		when(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.thenReturn(true);

		CustomException exception = assertThrows(CustomException.class, () -> {
			explorationService.startExploration(1L, 1L, new ExplorationStartRequest());
		});

		assertEquals(ErrorCode.ALREADY_STARTED_EXPLORATION, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 시작 - 유저 없음 예외 발생")
	void startExploration_userNotFound_throwException() {
		when(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.thenReturn(false);

		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () -> {
			explorationService.startExploration(1L, 1L, new ExplorationStartRequest());
		});

		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 시작 - 장소 없음 예외 발생")
	void startExploration_placeNotFound_throwException() {
		when(explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(anyLong(), anyLong(), any(LocalDateTime.class)))
			.thenReturn(false);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
		when(placeRepository.findById(anyLong())).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () -> {
			explorationService.startExploration(1L, 1L, new ExplorationStartRequest());
		});

		assertEquals(ErrorCode.PLACE_NOT_FOUND, exception.getErrorCode());
	}


	// 탐험 제한 시간 연장
	@Test
	@DisplayName("탐험 제한 시간 연장 - 성공")
	void extendExplorationTime_success() {
		when(explorationRepository.findById(1L)).thenReturn(Optional.of(exploration));

		ExplorationExtendTimeResponse response = explorationService.extendExplorationTime(1L, 1L);

		assertEquals(1L, response.getExplorationId());
		assertTrue(response.getNewDeadline().isAfter(LocalDateTime.now()));

		verify(explorationRepository).findById(1L);
	}

	@Test
	@DisplayName("탐험 제한 시간 연장 - 탐험 없음 예외")
	void extendExplorationTime_explorationNotFound() {
		when(explorationRepository.findById(1L)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class,
												 () -> explorationService.extendExplorationTime(1L, 1L));

		assertEquals(ErrorCode.EXPLORATION_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 제한 시간 연장 - 권한 없음 예외")
	void extendExplorationTime_noPermission() {
		User otherUser = User.builder().id(2L).build();
		exploration.setUser(otherUser);

		when(explorationRepository.findById(1L)).thenReturn(Optional.of(exploration));

		CustomException exception = assertThrows(CustomException.class,
												 () -> explorationService.extendExplorationTime(1L, 1L));

		assertEquals(ErrorCode.EXPLORATION_NO_PERMISSION, exception.getErrorCode());
	}

	@Test
	@DisplayName("탐험 제한 시간 연장 - 이미 종료된 탐험 예외")
	void extendExplorationTime_alreadyEnded() {
		exploration.setEndAt(LocalDateTime.now().minusMinutes(1));
		when(explorationRepository.findById(1L)).thenReturn(Optional.of(exploration));

		CustomException exception = assertThrows(CustomException.class,
												 () -> explorationService.extendExplorationTime(1L, 1L));

		assertEquals(ErrorCode.EXPLORATION_ALREADY_ENDED, exception.getErrorCode());
	}
}