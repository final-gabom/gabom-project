package com.explorer.gabom.domain.place.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceCreateRequest(

	@NotBlank
	String title,

	@NotBlank
	String address,

	@NotNull
	Double lat,

	@NotNull
	Double lng,

	@NotBlank
	String content,

	@NotBlank
	String proofMethod,

	List<Long> imageIds

) {
}