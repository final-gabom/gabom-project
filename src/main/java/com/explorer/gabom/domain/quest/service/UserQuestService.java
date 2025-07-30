package com.explorer.gabom.domain.quest.service;

import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.quest.dto.UserQuestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestRewardResponse;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.dto.PageResponse;

public interface UserQuestService {

	void updateProgress(User user, QuestConditionType type, int step);

	QuestRewardResponse claimReward(Long userId, Long userQuestId);

	PageResponse<UserQuestDto> getProgress(Long userId, ProgressStatus progressStatus, Pageable pageable);
}
