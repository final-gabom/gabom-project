package com.explorer.gabom.domain.ranking.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpEventMessage {
	private Long userId;
	private int exp;
	private int level;
	private String nickname;
	private String titleName;
	private String profileImgId;
}
