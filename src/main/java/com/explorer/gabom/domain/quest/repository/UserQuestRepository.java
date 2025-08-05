package com.explorer.gabom.domain.quest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.user.entity.User;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long>, UserQuestRepositoryCustom {

	Optional<UserQuest> findByUserAndQuest(User user, Quest quest);

	List<UserQuest> findAllByQuestAndQuest_DeletedFalse(Quest quest);

	List<UserQuest> findAllByQuest(Quest quest);

	Optional<UserQuest> findByUser_IdAndIdAndQuest_DeletedFalse(Long userId, Long id);

}
