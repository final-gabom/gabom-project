package com.explorer.gabom.domain.level.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.level.entity.Level;
import com.explorer.gabom.domain.level.repository.LevelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LevelService {

	private final LevelRepository levelRepository;

	public int calculateLevel(Long currentExp) {
		List<Level> levels = levelRepository.findAllByOrderByRequiredExpAsc();
		int userLevel = 1;

		for (Level level : levels) {
			if (currentExp >= level.getRequiredExp()) {
				userLevel = level.getLevel();
			} else {
				break;
			}
		}

		return userLevel;
	}
}
