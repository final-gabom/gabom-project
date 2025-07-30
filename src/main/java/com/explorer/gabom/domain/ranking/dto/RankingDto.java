package com.explorer.gabom.domain.ranking.dto;

import com.explorer.gabom.domain.ranking.entity.Ranking;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingDto {
	private int rank;
	private Long userId;
	private String nickname;
	private String profileImageUrl;
	private int level;
	private int exp;
	private String titleName;

	public static RankingDto toDto(Ranking ranking) {
		return RankingDto.builder()
						 .rank(ranking.getRank())
						 .userId(ranking.getUserId())
						 .nickname(ranking.getNickname())
						 .profileImageUrl(ranking.getProfileImageUrl())
						 .level(ranking.getLevel())
						 .exp(ranking.getExp())
						 .titleName(ranking.getTitleName())
						 .build();
	}
}
