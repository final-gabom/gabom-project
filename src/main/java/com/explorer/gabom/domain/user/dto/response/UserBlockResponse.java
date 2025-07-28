package com.explorer.gabom.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBlockResponse {
	private final Long blockerId;
	private final Long blockedId;
}
