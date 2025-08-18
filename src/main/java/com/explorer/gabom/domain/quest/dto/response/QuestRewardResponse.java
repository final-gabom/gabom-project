package com.explorer.gabom.domain.quest.dto.response;

import com.explorer.gabom.domain.quest.entity.Quest;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestRewardResponse {
	private Long rewardPoint;
	private Long rewardExp;
	private Long rewardTitleId;

	public static QuestRewardResponse toDto(Quest quest) {
		return QuestRewardResponse.builder()
								  .rewardPoint(quest.getRewardPoint())
								  .rewardExp(quest.getRewardExp())
								  .rewardTitleId(quest.getRewardTitle() != null ? quest.getRewardTitle().getId() : null)
								  .build();
	}
}
