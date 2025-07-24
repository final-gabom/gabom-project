package com.explorer.gabom.domain.quest.dto.request;

import com.explorer.gabom.domain.quest.type.QuestConditionType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestUpdateRequestDto {

	private String title;

	private String description;

	private QuestConditionType questConditionType;

	private Integer acquireCondition;

	private Integer rewardPoint;

	private Integer rewardExp;

	private Long rewardTitleId;
}
