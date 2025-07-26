package com.explorer.gabom.domain.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class PlaceListResponse {
	private Long placeId;
	private String title;
	private String address;
	private int point;
	private String content;
	private double distance; // km 단위


}
