package com.explorer.gabom.domain.Exploration.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.Exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.Exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.Exploration.repository.ExplorationRepository;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

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
		Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
	}
}
