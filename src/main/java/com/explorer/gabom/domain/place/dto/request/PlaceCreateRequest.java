package com.explorer.gabom.domain.place.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaceCreateRequest {

	private final String title;
	private final String address;
	private final Double lat;
	private final Double lng;
	private final String content;
	private final String proofMethod;

	@Builder
	public PlaceCreateRequest(String title, String address, Double lat, Double lng,
							  String content, String proofMethod) {
		this.title = title;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.content = content;
		this.proofMethod = proofMethod;
	}
}