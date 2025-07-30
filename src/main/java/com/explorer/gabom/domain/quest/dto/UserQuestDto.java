package com.explorer.gabom.domain.quest.dto;

import java.time.LocalDateTime;

import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.type.ProgressStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserQuestDto {
	private Long userQuestId;
	private String questTitle;
	private ProgressStatus progressStatus;
	private int progressCount;
	private int acquireCondition;
	private boolean rewardClaimed;
	private LocalDateTime completedAt;

	public static UserQuestDto toDto(UserQuest userQuest) {
		return UserQuestDto.builder()
						   .userQuestId(userQuest.getId())
						   .questTitle(userQuest.getQuest().getTitle())
						   .progressStatus(userQuest.getProgressStatus())
						   .progressCount(userQuest.getProgressCount())
						   .acquireCondition(userQuest.getQuest().getAcquireCondition())
						   .rewardClaimed(userQuest.isRewardClaimed())
						   .completedAt(userQuest.getCompletedAt())
						   .build();
	}
}
