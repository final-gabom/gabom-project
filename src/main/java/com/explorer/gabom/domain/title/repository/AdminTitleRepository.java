package com.explorer.gabom.domain.title.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.title.entity.Title;

public interface AdminTitleRepository extends JpaRepository<Title, Long> {
	boolean existsByName(String name);
}
