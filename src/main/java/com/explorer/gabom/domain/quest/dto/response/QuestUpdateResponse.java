package com.explorer.gabom.domain.quest.dto.response;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.global.dto.TargetIdentifiable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestUpdateResponse implements TargetIdentifiable {

	private Long questId;
	private String title;
	private String description;
	private QuestConditionType questConditionType;
	private int acquireCondition;
	private int rewardPoint;
	private int rewardExp;
	private Long rewardTitleId;
	private LocalDateTime updatedAt;

	public static QuestUpdateResponse toDto(Quest quest) {
		return QuestUpdateResponse.builder()
								  .questId(quest.getId())
								  .title(quest.getTitle())
								  .description(quest.getDescription())
								  .questConditionType(quest.getQuestConditionType())
								  .acquireCondition(quest.getAcquireCondition())
								  .rewardPoint(quest.getRewardPoint())
								  .rewardExp(quest.getRewardExp())
								  .rewardTitleId(quest.getRewardTitle().getId())
								  .updatedAt(quest.getUpdatedAt())
								  .build();
	}

	@Override
	public Long getTargetId() {
		return this.questId;
	}
}
