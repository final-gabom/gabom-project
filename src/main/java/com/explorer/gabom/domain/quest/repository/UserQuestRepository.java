package com.explorer.gabom.domain.quest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.user.entity.User;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {

	List<UserQuest> findByUserAndQuest_QuestConditionTypeAndProgressStatus(
		User user,
		QuestConditionType questConditionType,
		ProgressStatus progressStatus
	);

	Optional<UserQuest> findByUser_IdAndId(Long userId, Long id);

}
