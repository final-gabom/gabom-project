package com.explorer.gabom.domain.ranking.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpEventMessage {
	private Long userId;
	private Long exp;
}
