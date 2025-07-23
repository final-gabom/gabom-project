package com.explorer.gabom.domain.title.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TitleCreateRequest (
	@NotBlank(message = "칭호 이름을 입력해주세요.")
	String name,

	@NotBlank(message = "칭호 설명을 입력해주세요.")
	String description
) {}
