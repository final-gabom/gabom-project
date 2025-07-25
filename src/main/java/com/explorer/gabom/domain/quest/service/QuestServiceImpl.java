package com.explorer.gabom.domain.quest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.quest.dto.QuestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestPage;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

	private final QuestRepository questRepository;

	@Override
	public QuestPage getQuestPage(Pageable pageable) {
		Page<QuestDto> questDtoPage = questRepository.findAll(pageable)
													 .map(QuestDto::toDto);
		return QuestPage.toDto(questDtoPage);
	}

	@Override
	public QuestDto getQuestById(Long questId) {
		Quest quest = questRepository.findById(questId)
									 .orElseThrow(() -> new CustomException(ErrorCode.QUEST_NOT_FOUND));
		return QuestDto.toDto(quest);
	}

}
