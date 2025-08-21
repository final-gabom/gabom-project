package com.explorer.gabom.domain.user.dto.response;

import com.explorer.gabom.global.dto.TargetIdentifiable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateMainTitleResponse implements TargetIdentifiable {
	private final Long titleId;
	private final String titleName;

	@Override
	public Long getTargetId() {
		return this.titleId;
	}
}
