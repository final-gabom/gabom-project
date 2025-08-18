package com.explorer.gabom.domain.quest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequest;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequest;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestDeleteResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestUpdateResponse;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.domain.quest.repository.UserQuestRepository;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AdminQuestServiceTest {

	private static final Long TITLE_ID = 1L;
	private static final Long QUEST_ID = 1L;
	private static final Long NEW_TITLE_ID = 2L;

	private static final String QUEST_TITLE = "테스트 퀘스트";
	private static final String QUEST_DESCRIPTION = "퀘스트 설명";
	private static final QuestConditionType QUEST_CONDITION_TYPE = QuestConditionType.PLACE;
	private static final int QUEST_ACQUIRE_CONDITION = 5;
	private static final Long REWARD_POINT = 100;
	private static final Long REWARD_EXP = 50;

	private static final String UPDATED_TITLE = "수정된 제목";
	private static final String UPDATED_DESCRIPTION = "수정된 설명";
	private static final int UPDATED_ACQUIRE_CONDITION = 10;
	private static final int UPDATED_REWARD_POINT = 300;
	private static final int UPDATED_REWARD_EXP = 150;

	private Title title;

	@Mock
	private QuestRepository questRepository;

	@Mock
	private TitleRepository titleRepository;

	@Mock
	private UserQuestRepository userQuestRepository;

	@InjectMocks
	private AdminQuestServiceImpl adminQuestService;

	@BeforeEach
	void setup() {
		title = createTitle(TITLE_ID, "테스트 칭호", "칭호 설명");
	}

	@Test
	@DisplayName("퀘스트 생성 - 성공")
	void createQuest_success() {
		QuestCreateRequest request = createQuestCreateRequest(TITLE_ID);
		Quest savedQuest = createQuestFromRequest(request, QUEST_ID, title);

		given(titleRepository.findById(TITLE_ID)).willReturn(Optional.of(title));
		given(questRepository.save(any())).willReturn(savedQuest);

		QuestCreateResponse response = adminQuestService.createQuest(request);

		assertThat(response.getQuestId()).isEqualTo(QUEST_ID);
		assertThat(response.getTitle()).isEqualTo(QUEST_TITLE);
		assertThat(response.getDescription()).isEqualTo(QUEST_DESCRIPTION);
		assertThat(response.getQuestConditionType()).isEqualTo(QUEST_CONDITION_TYPE);
		assertThat(response.getAcquireCondition()).isEqualTo(QUEST_ACQUIRE_CONDITION);
		assertThat(response.getRewardPoint()).isEqualTo(REWARD_POINT);
		assertThat(response.getRewardExp()).isEqualTo(REWARD_EXP);
		assertThat(response.getRewardTitleId()).isEqualTo(TITLE_ID);

		ArgumentCaptor<Quest> captor = ArgumentCaptor.forClass(Quest.class);
		verify(questRepository).save(captor.capture());

		Quest captured = captor.getValue();
		assertThat(captured.getTitle()).isEqualTo(QUEST_TITLE);
		assertThat(captured.getDescription()).isEqualTo(QUEST_DESCRIPTION);
		assertThat(captured.getQuestConditionType()).isEqualTo(QUEST_CONDITION_TYPE);
		assertThat(captured.getAcquireCondition()).isEqualTo(QUEST_ACQUIRE_CONDITION);
		assertThat(captured.getRewardPoint()).isEqualTo(REWARD_POINT);
		assertThat(captured.getRewardExp()).isEqualTo(REWARD_EXP);
		assertThat(captured.getRewardTitle().getId()).isEqualTo(TITLE_ID);
	}

	@Test
	@DisplayName("퀘스트 생성 - 실패 (존재하지 않는 칭호 ID)")
	void createQuest_titleNotFound_fail() {
		QuestCreateRequest request = createQuestCreateRequest(999L);

		given(titleRepository.findById(999L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> adminQuestService.createQuest(request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.TITLE_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("퀘스트 수정 - 성공")
	void updateQuest_success() {
		Quest existingQuest = createQuest(QUEST_ID, title);
		Title newTitle = createTitle(NEW_TITLE_ID, "새 칭호", "새 설명");

		QuestUpdateRequest request = createQuestUpdateRequest();

		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.of(existingQuest));
		given(titleRepository.findById(NEW_TITLE_ID)).willReturn(Optional.of(newTitle));

		QuestUpdateResponse response = adminQuestService.updateQuest(QUEST_ID, request);

		assertThat(response.getQuestId()).isEqualTo(QUEST_ID);
		assertThat(response.getTitle()).isEqualTo(UPDATED_TITLE);
		assertThat(response.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
		assertThat(response.getQuestConditionType()).isEqualTo(QUEST_CONDITION_TYPE);
		assertThat(response.getAcquireCondition()).isEqualTo(UPDATED_ACQUIRE_CONDITION);
		assertThat(response.getRewardPoint()).isEqualTo(UPDATED_REWARD_POINT);
		assertThat(response.getRewardExp()).isEqualTo(UPDATED_REWARD_EXP);
		assertThat(response.getRewardTitleId()).isEqualTo(NEW_TITLE_ID);
	}

	@Test
	@DisplayName("퀘스트 수정 - 실패 (퀘스트 없음)")
	void updateQuest_notFound_fail() {
		QuestUpdateRequest request = createQuestUpdateRequest();

		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.empty());

		assertThatThrownBy(() -> adminQuestService.updateQuest(QUEST_ID, request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.QUEST_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("퀘스트 삭제 - 성공")
	void deleteQuest_success() {
		Quest quest = createQuest(QUEST_ID, title);
		UserQuest userQuest = mock(UserQuest.class);

		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.of(quest));

		QuestDeleteResponse response = adminQuestService.deleteQuest(QUEST_ID);

		assertThat(response.getQuestId()).isEqualTo(QUEST_ID);
		verify(questRepository).findByIdAndDeletedFalse(QUEST_ID);
		verify(userQuestRepository).bulkDeleteByQuest(any(Quest.class));
	}

	@Test
	@DisplayName("퀘스트 삭제 - 실패 (퀘스트 없음)")
	void deleteQuest_notFound_fail() {
		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.empty());

		assertThatThrownBy(() -> adminQuestService.deleteQuest(QUEST_ID))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.QUEST_NOT_FOUND.getMessage());
	}

	private QuestCreateRequest createQuestCreateRequest(Long rewardTitleId) {
		QuestCreateRequest request = new QuestCreateRequest();
		ReflectionTestUtils.setField(request, "title", QUEST_TITLE);
		ReflectionTestUtils.setField(request, "description", QUEST_DESCRIPTION);
		ReflectionTestUtils.setField(request, "questConditionType", QUEST_CONDITION_TYPE);
		ReflectionTestUtils.setField(request, "acquireCondition", QUEST_ACQUIRE_CONDITION);
		ReflectionTestUtils.setField(request, "rewardPoint", REWARD_POINT);
		ReflectionTestUtils.setField(request, "rewardExp", REWARD_EXP);
		ReflectionTestUtils.setField(request, "rewardTitleId", rewardTitleId);
		return request;
	}

	private QuestUpdateRequest createQuestUpdateRequest() {
		QuestUpdateRequest request = new QuestUpdateRequest();
		ReflectionTestUtils.setField(request, "title", UPDATED_TITLE);
		ReflectionTestUtils.setField(request, "description", UPDATED_DESCRIPTION);
		ReflectionTestUtils.setField(request, "questConditionType", QUEST_CONDITION_TYPE);
		ReflectionTestUtils.setField(request, "acquireCondition", UPDATED_ACQUIRE_CONDITION);
		ReflectionTestUtils.setField(request, "rewardPoint", UPDATED_REWARD_POINT);
		ReflectionTestUtils.setField(request, "rewardExp", UPDATED_REWARD_EXP);
		ReflectionTestUtils.setField(request, "rewardTitleId", NEW_TITLE_ID);
		return request;
	}

	private Quest createQuestFromRequest(QuestCreateRequest request, Long id, Title title) {
		Quest quest = new Quest(
			request.getTitle(),
			request.getDescription(),
			request.getQuestConditionType(),
			request.getAcquireCondition(),
			request.getRewardPoint(),
			request.getRewardExp(),
			title
		);
		ReflectionTestUtils.setField(quest, "id", id);
		return quest;
	}

	private Quest createQuest(Long id, Title title) {
		Quest quest = new Quest(
			QUEST_TITLE,
			QUEST_DESCRIPTION,
			QUEST_CONDITION_TYPE,
			QUEST_ACQUIRE_CONDITION,
			REWARD_POINT,
			REWARD_EXP,
			title
		);
		ReflectionTestUtils.setField(quest, "id", id);
		return quest;
	}

	private Title createTitle(Long id, String name, String description) {
		Title title = new Title(name, description);
		ReflectionTestUtils.setField(title, "id", id);
		return title;
	}
}
