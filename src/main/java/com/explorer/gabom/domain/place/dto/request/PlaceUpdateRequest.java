package com.explorer.gabom.domain.place.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceUpdateRequest {

	private String title;
	private String address;
	private Double lat;
	private Double lng;
	private String content;
	private String proofMethod;
}
