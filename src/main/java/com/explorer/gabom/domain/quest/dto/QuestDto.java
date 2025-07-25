package com.explorer.gabom.domain.quest.dto;

import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.type.QuestConditionType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestDto {

	private Long questId;
	private String title;
	private String description;
	private QuestConditionType questConditionType;
	private int acquireCondition;
	private int rewardPoint;
	private int rewardExp;
	private Long rewardTitleId;

	public static QuestDto toDto(Quest quest) {
		return QuestDto.builder()
					   .questId(quest.getId())
					   .title(quest.getTitle())
					   .description(quest.getDescription())
					   .questConditionType(quest.getQuestConditionType())
					   .acquireCondition(quest.getAcquireCondition())
					   .rewardPoint(quest.getRewardPoint())
					   .rewardExp(quest.getRewardExp())
					   .rewardTitleId(quest.getRewardTitle().getId())
					   .build();
	}
}
