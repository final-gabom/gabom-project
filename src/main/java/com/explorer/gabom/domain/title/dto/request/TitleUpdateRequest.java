package com.explorer.gabom.domain.title.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "칭호 수정 요청 정보")
public class TitleUpdateRequest {
	@NotBlank(message = "칭호 이름은 필수입니다.")
	@Schema(description = "수정할 칭호 이름")
	private String name;

	@NotBlank(message = "칭호 설명은 필수입니다.")
	@Schema(description = "수정할 칭호 설명")
	private String description;


}
