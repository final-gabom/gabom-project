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
	private int level;
	private int exp;
	private Long titleId;
	private String profileImageId;

	public static RankingDto toDto(Ranking ranking) {
		return RankingDto.builder()
						 .rankNo(ranking.getRankNo() != null ? ranking.getRankNo() : 0)
						 .userId(ranking.getUserId())
						 .nickname(ranking.getNickname())
						 .level(ranking.getLevel())
						 .exp(ranking.getExp())
						 .titleId(ranking.getTitle() != null ? ranking.getTitle().getId() : null)
						 .profileImageId(
							 ranking.getProfileImage() != null ? ranking.getProfileImage().getFileId() : null)
						 .build();
	}
}
