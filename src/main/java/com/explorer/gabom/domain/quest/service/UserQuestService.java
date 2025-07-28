package com.explorer.gabom.domain.quest.service;

import com.explorer.gabom.domain.quest.dto.response.QuestRewardResponse;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.user.entity.User;

public interface UserQuestService {

	void updateProgress(User user, QuestConditionType type, int step);

	QuestRewardResponse claimReward(Long userId, Long userQuestId);
}
