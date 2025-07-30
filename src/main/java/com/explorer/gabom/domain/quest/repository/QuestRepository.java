package com.explorer.gabom.domain.quest.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.quest.entity.Quest;

public interface QuestRepository extends JpaRepository<Quest, Long> {

	Optional<Quest> findByIdAndDeletedFalse(Long id);

	Page<Quest> findAllByDeletedFalse(Pageable pageable);

}
