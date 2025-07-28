package com.explorer.gabom.domain.quest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.repository.UserQuestRepository;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestProgressService {

	private final UserQuestRepository userQuestRepository;

	public void updateProgress(User user, QuestConditionType type, int step) {
		List<UserQuest> userQuests = userQuestRepository
			.findByUserAndQuest_QuestConditionTypeAndProgressStatus(user, type, ProgressStatus.IN_PROGRESS);

		for (UserQuest userQuest : userQuests) {
			userQuest.increaseProgress(step);
		}

		userQuestRepository.saveAll(userQuests);
	}
}
