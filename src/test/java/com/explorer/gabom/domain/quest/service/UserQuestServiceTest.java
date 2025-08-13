package com.explorer.gabom.domain.quest.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.explorer.gabom.domain.level.service.LevelService;
import com.explorer.gabom.domain.quest.dto.UserQuestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestRewardResponse;
import com.explorer.gabom.domain.quest.entity.Quest;
import com.explorer.gabom.domain.quest.entity.UserQuest;
import com.explorer.gabom.domain.quest.repository.QuestRepository;
import com.explorer.gabom.domain.quest.repository.UserQuestRepository;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.ranking.message.ExpEventMessage;
import com.explorer.gabom.domain.ranking.message.ExpEventProducer;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class UserQuestServiceTest {

	private static final Long USER_ID = 1L;
	private static final Long USER_QUEST_ID = 2L;

	@InjectMocks
	private UserQuestServiceImpl userQuestService;

	@Mock
	private UserQuestRepository userQuestRepository;
	@Mock
	private QuestRepository questRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private LevelService levelService;
	@Mock
	private ExpEventProducer expEventProducer;
	@Mock
	private ExpEventMessage expEventMessage;

	private User user;
	private Quest quest;
	private UserQuest userQuest;
	private Pageable pageable;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10);
		user = mock(User.class);
		quest = mock(Quest.class);
		userQuest = spy(new UserQuest(user, quest));
	}

	@Test
	@DisplayName("퀘스트 진행도 업데이트 - 기존 UserQuest가 있을 때")
	void updateProgress_existingUserQuest() {
		when(questRepository.findByQuestConditionTypeAndDeletedFalse(QuestConditionType.PLACE))
			.thenReturn(List.of(quest));
		when(userQuestRepository.findByUserAndQuest(user, quest))
			.thenReturn(Optional.of(userQuest));

		userQuestService.updateProgress(user, QuestConditionType.PLACE, 1);

		verify(userQuest, times(1)).increaseProgress(1);
	}

	@Test
	@DisplayName("퀘스트 보상 수령 - 정상 케이스")
	void claimReward_success() {
		// given
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(userQuestRepository.findByUser_IdAndIdAndQuest_DeletedFalse(USER_ID, USER_QUEST_ID))
			.thenReturn(Optional.of(userQuest));

		when(userQuest.isCompleted()).thenReturn(true);
		when(userQuest.isRewardClaimed()).thenReturn(false);
		when(userQuest.getQuest()).thenReturn(quest);
		when(quest.getRewardExp()).thenReturn(100);
		when(quest.getRewardPoint()).thenReturn(50);
		when(quest.getRewardTitle()).thenReturn(null);

		when(user.getExp()).thenReturn(200); // 현재 경험치 세팅
		when(user.getLevel()).thenReturn(1);
		when(levelService.calculateLevel(200 + 100)).thenReturn(2); // 보상 경험치 합산 후 레벨 계산

		doNothing().when(expEventProducer).sendExpEvent(any(ExpEventMessage.class));

		// when
		QuestRewardResponse response = userQuestService.claimReward(USER_ID, USER_QUEST_ID);

		// then
		verify(userQuest).markRewardClaimed();
		verify(user).addExp(100);
		verify(user).addPoint(50);
		verify(user).updateLevel(2);
		verify(expEventProducer).sendExpEvent(any(ExpEventMessage.class));
		assertThat(response).isNotNull();
	}

	@Test
	@DisplayName("퀘스트 보상 수령 실패 - 완료되지 않은 퀘스트")
	void claimReward_notCompleted() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(userQuestRepository.findByUser_IdAndIdAndQuest_DeletedFalse(USER_ID, USER_QUEST_ID))
			.thenReturn(Optional.of(userQuest));
		when(userQuest.isCompleted()).thenReturn(false);

		CustomException e = assertThrows(CustomException.class,
										 () -> userQuestService.claimReward(USER_ID, USER_QUEST_ID));

		assertEquals(ErrorCode.NOT_COMPLETED, e.getErrorCode());
	}

	@Test
	@DisplayName("퀘스트 진행도 조회 - NOT_STARTED")
	void getProgress_notStarted() {
		Quest quest1 = mock(Quest.class);
		Quest quest2 = mock(Quest.class);
		Page<Quest> quests = new PageImpl<>(List.of(quest1, quest2));

		when(questRepository.findQuestsNotJoinedByUser(1L, pageable)).thenReturn(quests);

		PageResponse<UserQuestDto> response = userQuestService.getProgress(1L, ProgressStatus.NOT_STARTED, pageable);

		assertThat(response.getContent().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("퀘스트 진행도 조회 - IN_PROGRESS")
	void getProgress_inProgress() {
		UserQuest userQuest1 = mock(UserQuest.class);
		UserQuest userQuest2 = mock(UserQuest.class);
		Page<UserQuest> quests = new PageImpl<>(List.of(userQuest1, userQuest2));

		when(userQuestRepository.findUserQuests(1L, ProgressStatus.IN_PROGRESS, pageable)).thenReturn(quests);

		PageResponse<UserQuestDto> response = userQuestService.getProgress(1L, ProgressStatus.IN_PROGRESS, pageable);

		assertThat(response.getContent().size()).isEqualTo(2);
	}
}
