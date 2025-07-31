package com.explorer.gabom.domain.exploration.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationCurrentResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.exploration.entity.Exploration;
import com.explorer.gabom.domain.exploration.repository.ExplorationRepository;
import com.explorer.gabom.domain.exploration.vo.RewardCalculator;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.util.DistanceCalculator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExplorationService {

	private final UserRepository userRepository;
	private final PlaceRepository placeRepository;
	private final ExplorationRepository explorationRepository;

	// 탐험 시작
	@Transactional
	public ExplorationStartResponse startExploration(Long userId, Long placeId, ExplorationStartRequest request) {
		if (explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(userId, placeId, LocalDateTime.now())) {
			throw new CustomException(ErrorCode.ALREADY_STARTED_EXPLORATION);
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Place place = placeRepository.findById(placeId).orElseThrow(
			() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		double userLat = request.getLat();
		double userLng = request.getLng();

		double placeLat = place.getLat();
		double placeLng = place.getLng();

		double distance = DistanceCalculator.calculateKm(userLat, userLng, placeLat, placeLng);
		int rewardExp = RewardCalculator.calculate(distance);
		int rewardPoint = RewardCalculator.calculate(distance);

		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = startAt.plusHours(3); // TODO: 탐험 제한시간 갱신 기능 구현 시 수정 예정

		Exploration exploration = Exploration.builder()
											 .user(user)
											 .place(place)
											 .rewardExp(rewardExp)
											 .rewardPoint(rewardPoint)
											 .startAt(startAt)
											 .endAt(endAt)
											 .build();

		explorationRepository.save(exploration);

		return ExplorationStartResponse.builder()
									   .explorationId(exploration.getId())
									   .rewardPoint(rewardPoint)
									   .rewardExp(rewardExp)
									   .startAt(startAt)
									   .endAt(endAt)
									   .build();
	}

	// 탐험 중인 장소 조회
	public ExplorationCurrentResponse getCurrentExploration(Long userId) {
		Exploration exploration = explorationRepository
			.findTopByUserIdAndEndAtAfterOrderByStartedAtDesc(userId, LocalDateTime.now())
			.orElseThrow(() -> new CustomException(ErrorCode.NO_ACTIVE_EXPLORATION));

		Place place = exploration.getPlace();

		return ExplorationCurrentResponse.of(exploration, place);
	}
}
