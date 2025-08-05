package com.explorer.gabom.domain.quest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
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

	@Mock
	private QuestRepository questRepository;

	@Mock
	private TitleRepository titleRepository;

	@Mock
	private UserQuestRepository userQuestRepository;

	@InjectMocks
	private AdminQuestServiceImpl adminQuestService;

	private Title title;
	private static final Long TITLE_ID = 1L;
	private static final Long QUEST_ID = 1L;

	@BeforeEach
	void setup() {
		title = new Title("테스트 칭호", "칭호 설명");
		ReflectionTestUtils.setField(title, "id", TITLE_ID);
	}

	@Test
	@DisplayName("퀘스트 생성 - 성공")
	void createQuest_success() {
		// given
		QuestCreateRequest request = createQuestCreateRequest();
		Quest savedQuest = createQuestFromRequest(request);

		given(titleRepository.findById(TITLE_ID)).willReturn(Optional.of(title));
		given(questRepository.save(any())).willReturn(savedQuest);

		// when
		QuestCreateResponse response = adminQuestService.createQuest(request);

		// then
		assertThat(response.getQuestId()).isEqualTo(QUEST_ID);
		assertThat(response.getTitle()).isEqualTo("테스트 퀘스트");
		assertThat(response.getDescription()).isEqualTo("퀘스트 설명");
		assertThat(response.getQuestConditionType()).isEqualTo(QuestConditionType.PLACE);
		assertThat(response.getAcquireCondition()).isEqualTo(5);
		assertThat(response.getRewardPoint()).isEqualTo(100);
		assertThat(response.getRewardExp()).isEqualTo(50);
		assertThat(response.getRewardTitleId()).isEqualTo(TITLE_ID);

		ArgumentCaptor<Quest> captor = ArgumentCaptor.forClass(Quest.class);
		verify(questRepository).save(captor.capture());

		Quest captured = captor.getValue();
		assertThat(captured.getTitle()).isEqualTo("테스트 퀘스트");
		assertThat(captured.getDescription()).isEqualTo("퀘스트 설명");
		assertThat(captured.getQuestConditionType()).isEqualTo(QuestConditionType.PLACE);
		assertThat(captured.getAcquireCondition()).isEqualTo(5);
		assertThat(captured.getRewardPoint()).isEqualTo(100);
		assertThat(captured.getRewardExp()).isEqualTo(50);
		assertThat(captured.getRewardTitle().getId()).isEqualTo(TITLE_ID);
	}

	@Test
	@DisplayName("퀘스트 생성 - 실패 (존재하지 않는 칭호 ID)")
	void createQuest_titleNotFound_fail() {
		// given
		QuestCreateRequest request = createQuestCreateRequest();
		ReflectionTestUtils.setField(request, "rewardTitleId", 999L);

		given(titleRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminQuestService.createQuest(request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.TITLE_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("퀘스트 수정 - 성공")
	void updateQuest_success() {
		// given
		Quest existingQuest = createQuest(QUEST_ID, title);
		Title newTitle = new Title("새 칭호", "새 설명");
		ReflectionTestUtils.setField(newTitle, "id", 2L);

		QuestUpdateRequest request = createQuestUpdateRequest();

		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.of(existingQuest));
		given(titleRepository.findById(2L)).willReturn(Optional.of(newTitle));
		given(userQuestRepository.findAllByQuestAndQuest_DeletedFalse(any())).willReturn(List.of());

		// when
		QuestUpdateResponse response = adminQuestService.updateQuest(QUEST_ID, request);

		// then
		assertThat(response.getQuestId()).isEqualTo(QUEST_ID);
		assertThat(response.getTitle()).isEqualTo("수정된 제목");
		assertThat(response.getDescription()).isEqualTo("수정된 설명");
		assertThat(response.getQuestConditionType()).isEqualTo(QuestConditionType.PLACE);
		assertThat(response.getAcquireCondition()).isEqualTo(10);
		assertThat(response.getRewardPoint()).isEqualTo(300);
		assertThat(response.getRewardExp()).isEqualTo(150);
		assertThat(response.getRewardTitleId()).isEqualTo(2L);
	}

	@Test
	@DisplayName("퀘스트 수정 - 실패 (퀘스트 없음)")
	void updateQuest_notFound_fail() {
		// given
		QuestUpdateRequest request = createQuestUpdateRequest();
		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminQuestService.updateQuest(QUEST_ID, request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.QUEST_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("퀘스트 삭제 - 성공")
	void deleteQuest_success() {
		// given
		Quest quest = createQuest(QUEST_ID, title);
		UserQuest userQuest = mock(UserQuest.class);

		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.of(quest));
		given(userQuestRepository.findAllByQuest(quest)).willReturn(List.of(userQuest));

		// when
		QuestDeleteResponse response = adminQuestService.deleteQuest(QUEST_ID);

		// then
		assertThat(response.getQuestId()).isEqualTo(QUEST_ID);
		verify(userQuest).markAsDeleted();
	}

	@Test
	@DisplayName("퀘스트 삭제 - 실패 (퀘스트 없음)")
	void deleteQuest_notFound_fail() {
		// given
		given(questRepository.findByIdAndDeletedFalse(QUEST_ID)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> adminQuestService.deleteQuest(QUEST_ID))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.QUEST_NOT_FOUND.getMessage());
	}

	private QuestCreateRequest createQuestCreateRequest() {
		QuestCreateRequest request = new QuestCreateRequest();
		ReflectionTestUtils.setField(request, "title", "테스트 퀘스트");
		ReflectionTestUtils.setField(request, "description", "퀘스트 설명");
		ReflectionTestUtils.setField(request, "questConditionType", QuestConditionType.PLACE);
		ReflectionTestUtils.setField(request, "acquireCondition", 5);
		ReflectionTestUtils.setField(request, "rewardPoint", 100);
		ReflectionTestUtils.setField(request, "rewardExp", 50);
		ReflectionTestUtils.setField(request, "rewardTitleId", TITLE_ID);
		return request;
	}

	private QuestUpdateRequest createQuestUpdateRequest() {
		QuestUpdateRequest request = new QuestUpdateRequest();
		ReflectionTestUtils.setField(request, "title", "수정된 제목");
		ReflectionTestUtils.setField(request, "description", "수정된 설명");
		ReflectionTestUtils.setField(request, "questConditionType", QuestConditionType.PLACE);
		ReflectionTestUtils.setField(request, "acquireCondition", 10);
		ReflectionTestUtils.setField(request, "rewardPoint", 300);
		ReflectionTestUtils.setField(request, "rewardExp", 150);
		ReflectionTestUtils.setField(request, "rewardTitleId", 2L);
		return request;
	}

	private Quest createQuest(Long id, Title title) {
		Quest quest = new Quest("테스트 퀘스트", "퀘스트 설명", QuestConditionType.PLACE, 5, 100, 50, title);
		ReflectionTestUtils.setField(quest, "id", id);
		return quest;
	}

	private Quest createQuestFromRequest(QuestCreateRequest request) {
		Quest quest = new Quest(request.getTitle(), request.getDescription(), request.getQuestConditionType(),
								request.getAcquireCondition(), request.getRewardPoint(), request.getRewardExp(),
								title);
		ReflectionTestUtils.setField(quest, "id", QUEST_ID);
		return quest;
	}
}
