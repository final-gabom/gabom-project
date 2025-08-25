package com.explorer.gabom.domain.exploration.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.activity.aop.ActivityLoggable;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationCurrentResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationDetailResponse;
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

	private final UserRepository userRepository;
	private final PlaceRepository placeRepository;
	private final ExplorationRepository explorationRepository;
	private final AuthorValidator authorValidator;
	private final ExplorationAlarmScheduler alarmScheduler;
	// 운영 기본 만료시간(시간)
	@Value("${spring.exploration.duration.hours:3}")
	private long explorationDurationHours;
	// 로컬 즉시 확인용(초) – 설정되면 hours 대신 이 값 우선
	@Value("${spring.exploration.test.duration-seconds:0}")
	private long testDurationSeconds;

	// 탐험 시작
	@Transactional
	@ActivityLoggable(ActivityType.START_EXPLORATION)
	public ExplorationStartResponse startExploration(User user, Long placeId, ExplorationStartRequest request) {
		if (explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(user.getId(), placeId, LocalDateTime.now())) {
			throw new CustomException(ErrorCode.ALREADY_STARTED_EXPLORATION);
		}

		Place place = placeRepository.findById(placeId).orElseThrow(
			() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		// 리워드 계산
		double userLat = request.getLat();
		double userLng = request.getLng();

		double placeLat = place.getAddress().getLat();
		double placeLng = place.getAddress().getLng();

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
	@ActivityLoggable(ActivityType.EXTEND_EXPLORATION_TIME)
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

		// 이미 종료된 탐험인 경우
		if (exploration.getEndAt().isBefore(LocalDateTime.now())) {
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
	public List<ExplorationCurrentResponse> getCurrentExploration(Long userId) {
		List<Exploration> explorations = explorationRepository
			.findAllByUserIdAndEndAtAfterOrderByEndAtAsc(userId, LocalDateTime.now());

		if (explorations.isEmpty()) {
			throw new CustomException(ErrorCode.NO_ACTIVE_EXPLORATION);
		}

		return explorations.stream()
						   .map(exploration -> ExplorationCurrentResponse.of(exploration, exploration.getPlace()))
						   .toList();
	}

	// 탐험 장소 상세 조회
	@Transactional(readOnly = true)
	public ExplorationDetailResponse getExplorationDetail(Long explorationId) {
		Exploration exploration = explorationRepository.findById(explorationId)
													   .orElseThrow(
														   () -> new CustomException(ErrorCode.EXPLORATION_NOT_FOUND));

		return ExplorationDetailResponse.toDto(exploration);
	}

	@Transactional
	public void completeOrCreateCompleted(Long userId, Long placeId) {
		// 최신 1건 비관 잠금
		Optional<Exploration> opt = explorationRepository.findByUserIdAndPlaceIdAndStatus(userId, placeId, Exploration.Status.IN_PROGRESS);
		LocalDateTime now = LocalDateTime.now();

		if (opt.isPresent()) {
			Exploration e = opt.get();
			e.setStatus(Exploration.Status.COMPLETED);
			e.setEndAt(now);
			if (e.getStartAt() == null) e.setStartAt(now); // 안전장치
			return;
		}

		// 없으면 생성 → 즉시 완료
		Place place = placeRepository.findById(placeId)
									 .orElseThrow(() -> new IllegalArgumentException("Place not found: " + placeId));

		Exploration created = Exploration.builder()
										 .user(User.builder().id(userId).build()) // 이미 영속 User가 있다면 그대로 넘겨 사용 가능
										 .place(place)
										 .startAt(now)
										 .endAt(now)
										 .status(Exploration.Status.COMPLETED)
										 .rewardPoint(50)
										 .rewardExp(50)
										 .build();

		explorationRepository.save(created);
	}
}

