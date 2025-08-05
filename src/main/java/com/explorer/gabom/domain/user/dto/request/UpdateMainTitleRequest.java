package com.explorer.gabom.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateMainTitleRequest {
	@Schema(description = "변경할 칭호 ID", example = "1")
	private final Long titleId;
}
