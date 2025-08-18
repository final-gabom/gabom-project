package com.explorer.gabom.domain.quest.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.global.dto.TargetIdentifiable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestCreateResponse implements TargetIdentifiable {
	private Long questId;
	private String title;
	private String description;
	private QuestConditionType questConditionType;
	private int acquireCondition;
	private Long rewardPoint;
	private Long rewardExp;
	private Long rewardTitleId;
	private LocalDateTime createdAt;

	public static QuestCreateResponse toDto(Quest quest) {
		return QuestCreateResponse.builder()
								  .questId(quest.getId())
								  .title(quest.getTitle())
								  .description(quest.getDescription())
								  .questConditionType(quest.getQuestConditionType())
								  .acquireCondition(quest.getAcquireCondition())
								  .rewardPoint(quest.getRewardPoint())
								  .rewardExp(quest.getRewardExp())
								  .rewardTitleId(quest.getRewardTitle().getId())
								  .createdAt(quest.getCreatedAt())
								  .build();
	}

	@Override
	public Long getTargetId() {
		return this.questId;
	}
}
