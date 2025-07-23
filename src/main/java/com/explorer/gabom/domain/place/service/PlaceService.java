package com.explorer.gabom.domain.place.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.place.dto.request.PlaceCreateRequest;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;

	// 탐험 장소 생성
	public void createPlace(PlaceCreateRequest request /*, User user */) {
		// TODO: 인증 유저 정보 받아서 연관관계 처리 팔요
		Place place = new Place();

		place.setTitle(request.title());
		place.setAddress(request.address());
		place.setLat(request.lat());
		place.setLng(request.lng());
		place.setContent(request.content());
		place.setProofMethod(request.proofMethod());
		place.setViewCount(0);

		// TODO: user 설정 필요
		// TODO: 이미지 파일 연관관계 설정 필요

		placeRepository.save(place);
	}
}
