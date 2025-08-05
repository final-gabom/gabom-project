package com.explorer.gabom.domain.exploration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExplorationStartRequest {

	private double lat;
	private double lng;

}
