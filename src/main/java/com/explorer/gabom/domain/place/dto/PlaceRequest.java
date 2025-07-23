package com.explorer.gabom.domain.place.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceRequest(

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