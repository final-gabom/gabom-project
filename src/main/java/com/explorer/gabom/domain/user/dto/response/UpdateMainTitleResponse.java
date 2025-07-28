package com.explorer.gabom.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateMainTitleResponse {
	private final Long titleId;
	private final String titleName;
}
