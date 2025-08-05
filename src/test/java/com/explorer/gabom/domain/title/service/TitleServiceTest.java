package com.explorer.gabom.domain.title.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleDeleteResponse;
import com.explorer.gabom.domain.title.dto.response.UserTitleResponse;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.entity.UserTitle;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("TitleService 단위 테스트")
class TitleServiceTest {

	private static final Long VALID_ID = 1L;
	private static final Long INVALID_ID = 404L;
	private static final String TITLE_NAME = "탐험가";
	private static final String TITLE_DESC = "누적 10km 이동";

	@InjectMocks
	private TitleService titleService;

	@Mock
	private TitleRepository titleRepository;

	@Mock
	private UserRepository userRepository;

	@Test
	@DisplayName("createTitle - 성공")
	void createTitle_success() {
		TitleCreateRequest request = new TitleCreateRequest(TITLE_NAME, TITLE_DESC);
		Title saved = createTitle(TITLE_NAME, TITLE_DESC, VALID_ID);

		given(titleRepository.existsByName(TITLE_NAME)).willReturn(false);
		given(titleRepository.save(any(Title.class))).willReturn(saved);

		TitleCreateResponse response = titleService.createTitle(request);

		assertEquals(VALID_ID, response.getId());
		assertEquals(TITLE_NAME, response.getName());
	}

	@Test
	@DisplayName("createTitle - 실패 (이미 존재)")
	void createTitle_fail_alreadyExists() {
		TitleCreateRequest request = new TitleCreateRequest(TITLE_NAME, "중복");

		given(titleRepository.existsByName(TITLE_NAME)).willReturn(true);

		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.createTitle(request));
		assertEquals(ErrorCode.TITLE_ALREADY_EXISTS, e.getErrorCode());
	}

	@Test
	@DisplayName("updateTitle - 성공")
	void updateTitle_success() {
		TitleUpdateRequest request = new TitleUpdateRequest("수정된 이름", "수정된 설명");
		Title existing = createTitle("기존 이름", "기존 설명", VALID_ID);

		given(titleRepository.findById(VALID_ID)).willReturn(Optional.of(existing));

		titleService.updateTitle(VALID_ID, request);

		ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);

		verify(titleRepository).updateTitle(idCaptor.capture(), nameCaptor.capture(), descCaptor.capture());

		assertEquals(VALID_ID, idCaptor.getValue());
		assertEquals("수정된 이름", nameCaptor.getValue());
		assertEquals("수정된 설명", descCaptor.getValue());
	}

	@Test
	@DisplayName("updateTitle - 실패 (존재하지 않음)")
	void updateTitle_fail_notFound() {
		TitleUpdateRequest request = new TitleUpdateRequest("수정", "수정 설명");

		given(titleRepository.findById(INVALID_ID)).willReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.updateTitle(INVALID_ID, request));
		assertEquals(ErrorCode.TITLE_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("deleteTitle - 성공")
	void deleteTitle_success() {
		Title title = createTitle("삭제할 제목", "설명", VALID_ID);

		given(titleRepository.findById(VALID_ID)).willReturn(Optional.of(title));

		TitleDeleteResponse response = titleService.deleteTitle(VALID_ID);

		ArgumentCaptor<Title> captor = ArgumentCaptor.forClass(Title.class);
		verify(titleRepository).delete(captor.capture());

		assertEquals(VALID_ID, response.getId());
		assertEquals(VALID_ID, captor.getValue().getId());
	}

	@Test
	@DisplayName("deleteTitle - 실패 (존재하지 않음)")
	void deleteTitle_fail_notFound() {
		given(titleRepository.findById(INVALID_ID)).willReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.deleteTitle(INVALID_ID));
		assertEquals(ErrorCode.TITLE_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("getUserTitles - 성공")
	void getUserTitles_success() {
		User user = createUser(VALID_ID);

		UserTitle userTitle = new UserTitle();
		ReflectionTestUtils.setField(userTitle, "id", 10L);

		Title title = createTitle("탐험가", "누적 10km 이동", 10L);
		ReflectionTestUtils.setField(userTitle, "title", title);

		ReflectionTestUtils.setField(user, "userTitles", List.of(userTitle));

		given(userRepository.findByIdAndStatus(VALID_ID, UserStatus.ACTIVE))
			.willReturn(Optional.of(user));

		// when
		List<UserTitleResponse> responses = titleService.getUserTitles(VALID_ID);

		// then
		assertEquals(1, responses.size());
		assertEquals(10L, responses.get(0).getId());
		assertEquals("탐험가", responses.get(0).getName());
	}


	@Test
	@DisplayName("getUserTitles - 실패 (존재하지 않는 유저)")
	void getUserTitles_fail_userNotFound() {
		given(userRepository.findByIdAndStatus(INVALID_ID, UserStatus.ACTIVE)).willReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
										 () -> titleService.getUserTitles(INVALID_ID));
		assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
	}

	private Title createTitle(String name, String description, Long id) {
		Title title = new Title(name, description);
		ReflectionTestUtils.setField(title, "id", id);
		return title;
	}

	private User createUser(Long id) {
		User user = new User();
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}
}
