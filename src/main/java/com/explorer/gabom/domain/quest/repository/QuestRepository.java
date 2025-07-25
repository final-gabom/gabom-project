package com.explorer.gabom.domain.quest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.explorer.gabom.domain.quest.entity.Quest;

public interface QuestRepository extends JpaRepository<Quest, Long> {

	@Query("""
		SELECT q
		FROM Quest q
		WHERE
		(:search IS NULL OR 
		LOWER(q.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
		LOWER(q.description) LIKE LOWER(CONCAT('%', :search, '%')))
		""")
	Page<Quest> findAllByFilters(
		Pageable pageable,
		@Param("search") String search
	);

}
