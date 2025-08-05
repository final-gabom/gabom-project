package com.explorer.gabom.domain.title.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.explorer.gabom.domain.title.dto.response.UserTitleResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserTitleController - 통합 테스트")
class UserTitleControllerTest {

	private static final Long VALID_USER_ID = 1L;
	private static final Long INVALID_USER_ID = 999L;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TitleService titleService;

	@MockBean
	private JwtProvider jwtProvider;

	@Autowired
	private ObjectMapper objectMapper;

	private List<UserTitleResponse> mockResponse;

	@BeforeEach
	void setUp() {
		mockResponse = createUserTitleResponseList();
	}

	@Test
	@DisplayName("성공 - 유저의 칭호 목록 조회")
	@WithMockUser(roles = "ADMIN")
	void getUserTitles_success() throws Exception {
		// given
		given(titleService.getUserTitles(VALID_USER_ID)).willReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/api/titles/users/{userId}", VALID_USER_ID))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 조회되었습니다."))
			   .andExpect(jsonPath("$.data").isArray())
			   .andExpect(jsonPath("$.data[0].id").value(10))
			   .andExpect(jsonPath("$.data[0].name").value("모험가"));

		// ArgumentCaptor 사용
		ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		then(titleService).should().getUserTitles(captor.capture());
		assertThat(captor.getValue()).isEqualTo(VALID_USER_ID);
	}

	@Test
	@DisplayName("실패 - 존재하지 않는 유저의 칭호 목록 조회")
	@WithMockUser(roles = "ADMIN")
	void getUserTitles_fail_notFound() throws Exception {
		// given
		given(titleService.getUserTitles(INVALID_USER_ID))
			.willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/api/titles/users/{userId}", INVALID_USER_ID))
			   .andExpect(status().isNotFound())
			   .andExpect(jsonPath("$.success").value(false))
			   .andExpect(jsonPath("$.message").value("해당 유저를 찾을 수 없습니다."));

		then(titleService).should().getUserTitles(INVALID_USER_ID);
	}

	// 중복 제거된 응답 생성 메서드
	private List<UserTitleResponse> createUserTitleResponseList() {
		return List.of(
			new UserTitleResponse(10L, "팀험가", "첫 칭호"),
			new UserTitleResponse(11L, "만렙탐험가", "많이 돌아다님")
		);
	}
}
