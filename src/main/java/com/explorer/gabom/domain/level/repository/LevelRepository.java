package com.explorer.gabom.domain.level.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.level.entity.Level;

public interface LevelRepository extends JpaRepository<Level, Integer> {
	List<Level> findAllByOrderByRequiredExpAsc();
}
