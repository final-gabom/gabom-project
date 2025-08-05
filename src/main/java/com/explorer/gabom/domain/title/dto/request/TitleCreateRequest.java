package com.explorer.gabom.domain.title.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleCreateRequest {
	@NotBlank(message = "칭호 이름을 입력해주세요.")
	@Schema(description = "등록할 칭호 이름")
	private String name;

	@NotBlank(message = "칭호 설명을 입력해주세요.")
	@Schema(description = "등록할 칭호 설명")
	private String description;
}
