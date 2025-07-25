package com.explorer.gabom.domain.place.dto.response;

import com.explorer.gabom.domain.place.entity.Place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlaceDetailResponse {

	private Long placeId;
	private String title;
	private String address;
	private String distance;
	private int point;
	private String proofMethod;
	private String content;
	private int viewCount;

	public static PlaceDetailResponse from(Place place) {
		return PlaceDetailResponse.builder()
			.placeId(place.getId())
			.title(place.getTitle())
			.address(place.getAddress())
			.distance("") // 아직 거리 구현 안해서 공백처리
			/* TODO : 포인트 생성 시 구현
			.point(place.getPoint())
			 */
			.proofMethod(place.getProofMethod())
			.content(place.getContent())
			.viewCount(place.getViewCount())
			.build();
	}
}
