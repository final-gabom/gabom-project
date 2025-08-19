package com.explorer.gabom.domain.quest.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.quest.dto.QuestDto;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

	private final QuestRepository questRepository;

	@Override
	public PageResponse<QuestDto> getQuestPage(Pageable pageable) {
		Page<QuestDto> questDtoPage = questRepository.findAllByDeletedFalse(pageable)
													 .map(QuestDto::toDto);
		return PageResponse.toDto(questDtoPage);
	}

	@Override
	public QuestDto getQuestById(Long questId) {
		Quest quest = questRepository.findByIdAndDeletedFalse(questId)
									 .orElseThrow(() -> new CustomException(ErrorCode.QUEST_NOT_FOUND));
		return QuestDto.toDto(quest);
	}



}
