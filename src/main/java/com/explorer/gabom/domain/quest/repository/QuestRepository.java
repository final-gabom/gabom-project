package com.explorer.gabom.domain.quest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.quest.entity.Quest;

public interface QuestRepository extends JpaRepository<Quest, Long> {
}
