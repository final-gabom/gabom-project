package com.explorer.gabom.domain.place.dto.response;


import com.explorer.gabom.domain.place.entity.Place;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PlaceListResponse {
	private Long placeId;
	private String title;
	private String address;
	private int point;
	private String content;

	public static PlaceListResponse from(Place place) {
		return PlaceListResponse.builder()
								.placeId(place.getId())
								.title(place.getTitle())
								.address(place.getAddress())
								.content(place.getContent())
								.build();

	}
}