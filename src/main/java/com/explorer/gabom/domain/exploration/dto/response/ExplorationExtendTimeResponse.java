package com.explorer.gabom.domain.exploration.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExplorationExtendTimeResponse {

	private Long explorationId;
	private LocalDateTime newDeadline;
}
