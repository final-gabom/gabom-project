package com.explorer.gabom.domain.ranking.dto;

import com.explorer.gabom.domain.ranking.entity.Ranking;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankingSummaryDto {
	private Long rankNo;
	private Long userId;
	private String nickname;
	private int level;
	private String titleName;
	private String profileImgUrl;

	public static RankingSummaryDto toDto(Ranking ranking, Long rankNo) {
		return RankingSummaryDto.builder()
								.rankNo(rankNo)
								.userId(ranking.getUser().getId())
								.nickname(ranking.getUser().getNickname())
								.level(ranking.getUser().getLevel())
								.titleName(
									ranking.getUser().getTitle() != null ? ranking.getUser().getTitle().getName()
																		 : null)
								.profileImgUrl(
									ranking.getUser().getProfileImg() != null ? ranking.getUser()
																					   .getProfileImg()
																					   .getFilePath()
																			  : null)
								.build();
	}
}
