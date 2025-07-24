package com.explorer.gabom.domain.place.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaceCreateRequest {

	private final String title;
	private final String address;
	private final Double lat;
	private final Double lng;
	private final String proofMethod;
	private final String content;

	@Builder
	public PlaceCreateRequest(String title, String address, Double lat, Double lng, String proofMethod,
							  String content) {
		this.title = title;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.proofMethod = proofMethod;
		this.content = content;
	}
}