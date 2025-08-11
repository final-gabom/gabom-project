package com.explorer.gabom.domain.exploration.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.explorer.gabom.global.scheduler.ExplorationAlarmScheduler;
import com.explorer.gabom.global.util.DistanceCalculator;
import com.explorer.gabom.global.util.RewardCalculator;
import com.explorer.gabom.global.validator.AuthorValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExplorationService {

	// 운영 기본 만료시간(시간)
	@Value("${spring.exploration.duration.hours:3}")
	private long explorationDurationHours;

	// 로컬 즉시 확인용(초) – 설정되면 hours 대신 이 값 우선
	@Value("${spring.exploration.test.duration-seconds:0}")
	private long testDurationSeconds;

	private final UserRepository userRepository;
	private final PlaceRepository placeRepository;
	private final ExplorationRepository explorationRepository;
	private final AuthorValidator authorValidator;
	private final ExplorationAlarmScheduler alarmScheduler;

	// 탐험 시작
	@Transactional
	public ExplorationStartResponse startExploration(Long userId, Long placeId, ExplorationStartRequest request) {

		// 같은 장소에 진행 중인 탐험이 있으면 막기
		if (explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(userId, placeId, LocalDateTime.now())) {
			throw new CustomException(ErrorCode.ALREADY_STARTED_EXPLORATION);
		}

		// 연관 엔티티 로드
		User user = userRepository.findById(userId).orElseThrow(
			() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Place place = placeRepository.findById(placeId).orElseThrow(
			() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		// 리워드 계산
		double userLat = request.getLat();
		double userLng = request.getLng();

		double placeLat = place.getLat();
		double placeLng = place.getLng();

		double distance = DistanceCalculator.calculateKm(userLat, userLng, placeLat, placeLng);
		int rewardExp = RewardCalculator.calculate(distance);
		int rewardPoint = RewardCalculator.calculate(distance);


		// 시간 설정 (로컬 테스트면 초 단위, 운영은 시간 단위)
		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = (testDurationSeconds > 0)
							  ? startAt.plusSeconds(testDurationSeconds)
							  : startAt.plusHours(explorationDurationHours);


		// 엔티티 생성 및 저장 (상태는 IN_PROGRESS 보장)
		Exploration exploration = Exploration.builder()
											 .user(user)
											 .place(place)
											 .rewardExp(rewardExp)
											 .rewardPoint(rewardPoint)
											 .startAt(startAt)
											 .endAt(endAt)
											 .status(Exploration.Status.IN_PROGRESS)
											 .build();

		explorationRepository.save(exploration);

		// 제한시간 알림 스케줄
		alarmScheduler.schedule(exploration);

		return ExplorationStartResponse.builder()
									   .explorationId(exploration.getId())
									   .rewardPoint(rewardPoint)
									   .rewardExp(rewardExp)
									   .startAt(startAt)
									   .endAt(endAt)
									   .build();
	}

	// 탐험 제한 시간 연장
	@Transactional
	public ExplorationExtendTimeResponse extendExplorationTime(Long userId, Long explorationId) {
		Exploration exploration = explorationRepository.findById(explorationId)
													   .orElseThrow(
														   () -> new CustomException(
															   ErrorCode.EXPLORATION_NOT_FOUND));

		// 탐험 권한이 없는 경우
		authorValidator.validateOwner(
			exploration.getUser().getId(),
			userId
		);

		if (!exploration.isActive() || exploration.getEndAt().isBefore(LocalDateTime.now())) {
			throw new CustomException(ErrorCode.EXPLORATION_ALREADY_ENDED);
		}

		exploration.extendDeadline();

		explorationRepository.save(exploration);
		// 알림 스케줄 갱신
		alarmScheduler.reschedule(exploration);
		return new ExplorationExtendTimeResponse(exploration.getId(), exploration.getEndAt());
	}

	// 탐험 중인 장소 조회
	@Transactional(readOnly = true)
	public ExplorationCurrentResponse getCurrentExploration(Long userId) {
		Exploration exploration = explorationRepository
			.findTopByUserIdAndEndAtAfterOrderByEndAtAsc(userId, LocalDateTime.now())
			.orElseThrow(() -> new CustomException(ErrorCode.NO_ACTIVE_EXPLORATION));

		Place place = exploration.getPlace();

		return ExplorationCurrentResponse.of(exploration, place);
	}
}

