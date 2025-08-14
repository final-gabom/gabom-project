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
	private String profileImgUrl;

	public static RankingDto toDto(Ranking ranking, int rankNo) {
		return RankingDto.builder()
						 .rankNo(rankNo)
						 .userId(ranking.getUser().getId())
						 .nickname(ranking.getUser().getNickname())
						 .level(ranking.getUser().getLevel())
						 .exp(ranking.getExp())
						 .titleId(ranking.getUser().getTitle() != null ? ranking.getUser().getTitle().getId() : null)
						 .profileImgUrl(
							 ranking.getUser().getProfileImg() != null ? ranking.getUser().getProfileImg().getFilePath()
																	   : null)
						 .build();
	}
}
