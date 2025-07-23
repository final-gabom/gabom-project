package com.explorer.gabom.domain.title.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TitleCreateRequest (
	@NotBlank
	String name,

	@NotBlank
	String description
) {}
