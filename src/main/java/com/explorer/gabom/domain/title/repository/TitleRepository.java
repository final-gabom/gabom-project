package com.explorer.gabom.domain.title.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.title.entity.Title;

public interface TitleRepository extends JpaRepository<Title, Long> {
	boolean existsByName(String name);

	Optional<Title> findById(Long Id);
}
