package com.explorer.gabom.domain.Exploration.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.Exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.Exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.Exploration.entity.Exploration;
import com.explorer.gabom.domain.Exploration.repository.ExplorationRepository;
import com.explorer.gabom.domain.Exploration.vo.RewardCalculator;
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

	@Transactional
	public ExplorationStartResponse startExploration(Long userId, Long placeId, ExplorationStartRequest request) {
		if (explorationRepository.existsByUserIdAndPlaceIdAndEndAtAfter(userId, placeId, LocalDateTime.now())) {
			throw new CustomException(ErrorCode.ALREADY_STARTED_EXPLORATION);
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Place place = placeRepository.findById(placeId).orElseThrow(
			() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

		double lat1 = user.getLat();
		double lng1 = user.getLng();
		double lat2 = place.getLat();
		double lng2 = place.getLng();

		double distance = DistanceCalculator.calculate(lat1, lng1, lat2, lng2);
		int rewardExp = RewardCalculator.calculate(distance);
		int rewardPoint = RewardCalculator.calculate(distance);

		LocalDateTime startAt = LocalDateTime.now();
		LocalDateTime endAt = startAt.plusHours(3); // TODO: 탐험 제한시간 갱신 기능 구현 시 수정 예정

		Exploration exploration = Exploration.builder()
											 .user(user)
											 .place(place)
											 .rewordExp(rewardExp)
											 .rewordPoint(rewardPoint)
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
}
