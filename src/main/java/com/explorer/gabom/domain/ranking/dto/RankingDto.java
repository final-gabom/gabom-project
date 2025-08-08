package com.explorer.gabom.domain.ranking.dto;

import com.explorer.gabom.domain.ranking.entity.Ranking;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingDto {
	private int rankNo;
	private Long userId;
	private String nickname;
	private String profileImageId;
	private int level;
	private int exp;
	private String titleName;

	public static RankingDto toDto(Ranking ranking) {
		return RankingDto.builder()
						 .rankNo(ranking.getRankNo())
						 .userId(ranking.getUserId())
						 .nickname(ranking.getNickname())
						 .profileImageId(ranking.getProfileImageId())
						 .level(ranking.getLevel())
						 .exp(ranking.getExp())
						 .titleName(ranking.getTitleName())
						 .build();
	}
}
