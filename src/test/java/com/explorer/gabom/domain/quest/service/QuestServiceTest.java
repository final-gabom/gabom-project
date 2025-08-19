package com.explorer.gabom.domain.quest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.explorer.gabom.domain.quest.dto.QuestDto;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class QuestServiceTest {

	private static final Long QUEST_ID = 1L;
	private static final String QUEST_TITLE = "테스트 퀘스트";
	private static final String QUEST_DESCRIPTION = "퀘스트 설명";
	private static final QuestConditionType QUEST_CONDITION_TYPE = QuestConditionType.PLACE_REGISTER;
	private static final int QUEST_ACQUIRE_CONDITION = 5;
	private static final Long REWARD_POINT = 100L;
	private static final Long REWARD_EXP = 50L;
	private Title title;
	private static final String TITLE_NAME = "테스트 칭호";
	private static final String TITLE_DESCRIPTION = "칭호 설명";

	@Mock
	private QuestRepository questRepository;

	@InjectMocks
	private QuestServiceImpl questService;

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10);
		title = new Title(TITLE_NAME, TITLE_DESCRIPTION);
		ReflectionTestUtils.setField(title, "id", 1L);
	}

	@Test
	@DisplayName("퀘스트 목록 조회 - 성공")
	void getQuestPage_success() {
		Quest quest = createQuest();
		Page<Quest> questPage = new PageImpl<>(List.of(quest), pageable, 1);

		given(questRepository.findAllByDeletedFalse(pageable)).willReturn(questPage);

		PageResponse<QuestDto> result = questService.getQuestPage(pageable);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo(QUEST_TITLE);
	}

	@Test
	@DisplayName("퀘스트 단건 조회 - 성공")
	void getQuestById_success() {
		Quest quest = createQuest();
		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.of(quest));

		QuestDto result = questService.getQuestById(QUEST_ID);

		assertThat(result.getTitle()).isEqualTo(QUEST_TITLE);
	}

	@Test
	@DisplayName("퀘스트 단건 조회 - 실패(존재하지 않음)")
	void getQuestById_notFound() {
		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.empty());

		assertThatThrownBy(() -> questService.getQuestById(QUEST_ID))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.QUEST_NOT_FOUND.getMessage());
	}

	private Quest createQuest() {
		Quest quest = new Quest(
			QUEST_TITLE,
			QUEST_DESCRIPTION,
			QUEST_CONDITION_TYPE,
			QUEST_ACQUIRE_CONDITION,
			REWARD_POINT,
			REWARD_EXP,
			title
		);
		ReflectionTestUtils.setField(quest, "id", QUEST_ID);
		return quest;
	}
}
