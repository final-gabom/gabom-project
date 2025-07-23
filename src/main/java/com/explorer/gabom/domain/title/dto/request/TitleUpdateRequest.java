package com.explorer.gabom.domain.title.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TitleUpdateRequest {
	@NotBlank(message = "칭호 이름은 필수입니다.")
	private String name;

	@NotBlank(message = "칭호 설명은 필수입니다.")
	private String description;


}
