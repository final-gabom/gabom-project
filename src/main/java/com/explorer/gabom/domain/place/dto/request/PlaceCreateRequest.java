package com.explorer.gabom.domain.place.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class PlaceCreateRequest {

	private final String title;
	private final String address;
	private final Double lat;
	private final Double lng;
	private final String proofMethod;
	private final String content;
}