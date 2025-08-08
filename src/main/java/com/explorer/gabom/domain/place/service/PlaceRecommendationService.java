package com.explorer.gabom.domain.place.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.place.dto.request.PlaceRecommendationRequest;
import com.explorer.gabom.domain.place.dto.PlaceSummary;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.querydsl.core.Tuple;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceRecommendationService {
	private static final int DEFAULT_SIZE = 4;
	private final PlaceRepository placeRepository;

	@Transactional(readOnly = true)
	public List<PlaceSummary> getRecommendedPlaces(PlaceRecommendationRequest request) {
		List<Tuple> tuples = placeRepository.findWithinRadius(
			request.getLat(), request.getLng(),
			request.getRadius().getMinKm(), request.getRadius().getMaxKm()
		);

		if (tuples.isEmpty()) {
			return Collections.emptyList();
		}

		Collections.shuffle(tuples);
		List<Tuple> picked = tuples.stream()
								   .limit(DEFAULT_SIZE)
								   .toList();

		// 뽑은 튜플의 결과를 map에 저장하여 distance 필드에 집어넣기 쉽게함.
		Map<Long, Double> distanceMap = picked.stream()
											  .collect(Collectors.toMap(
												  t -> t.get(0, Long.class),
												  t -> t.get(1, Double.class)
											  ));

		List<Long> ids = new ArrayList<>(distanceMap.keySet());

		List<Place> places = placeRepository.findByIdIn(ids);

		return places.stream()
					 .map(p -> PlaceSummary.toDto(p, distanceMap.get(p.getId())))
					 .toList();
	}
}
