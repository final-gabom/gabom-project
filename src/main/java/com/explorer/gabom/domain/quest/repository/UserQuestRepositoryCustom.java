package com.explorer.gabom.domain.quest.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;

public interface UserQuestRepositoryCustom {
	Optional<Integer> findMaxProgressByUserAndQuestType(Long userId, QuestConditionType type);

	Page<UserQuest> findUserQuests(Long userId, ProgressStatus status, Pageable pageable);
}
