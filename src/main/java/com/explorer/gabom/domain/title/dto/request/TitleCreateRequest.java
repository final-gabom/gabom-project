package com.explorer.gabom.domain.title.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleCreateRequest {
	@NotBlank(message = "칭호 이름을 입력해주세요.")
	private String name;

	@NotBlank(message = "칭호 설명을 입력해주세요.")
	private String description;
}
