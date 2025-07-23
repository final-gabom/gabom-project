package com.explorer.gabom.domain.place.dto;

import java.util.List;

public record PlaceRequest(

	String title,
	String address,
	Double lat,
	Double lng,
	String content,
	String proofMethod,
	List<Long> imageIds

) {
}