package com.explorer.gabom.domain.quest.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.type.QuestConditionType;

public interface QuestRepositoryCustom {
	Page<Quest> findQuestsNotJoinedByUser(Long userId, Pageable pageable);

	List<Quest> findByQuestConditionTypeAndDeletedFalse(QuestConditionType questConditionType);
}
