package com.explorer.gabom.domain.quest.dto;

import java.time.LocalDateTime;
import java.util.Optional;

import com.explorer.gabom.domain.quest.entity.Quest;
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
		if (userQuest == null) {
			return null;
		}
		Quest quest = userQuest.getQuest();

		return UserQuestDto.builder()
						   .userQuestId(userQuest.getId())
						   .questTitle(quest != null ? quest.getTitle() : null)
						   .progressStatus(Optional.ofNullable(userQuest.getProgressStatus()).orElse(ProgressStatus.NOT_STARTED))
						   .progressCount(userQuest.getProgressCount())
						   .acquireCondition(quest != null ? quest.getAcquireCondition() : 0)
						   .rewardClaimed(userQuest.isRewardClaimed())
						   .completedAt(userQuest.getCompletedAt())
						   .build();
	}

	public static UserQuestDto toDto(Quest quest) {
		if (quest == null) {
			return null;
		}
		return UserQuestDto.builder()
						   .userQuestId(null)
						   .questTitle(quest.getTitle())
						   .progressStatus(ProgressStatus.NOT_STARTED)
						   .progressCount(0)
						   .acquireCondition(quest.getAcquireCondition())
						   .rewardClaimed(false)
						   .completedAt(null)
						   .build();
	}

}
