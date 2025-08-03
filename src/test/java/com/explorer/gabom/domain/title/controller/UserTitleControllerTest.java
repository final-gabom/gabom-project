package com.explorer.gabom.domain.title.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class UserTitleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TitleService titleService;

	@MockBean
	private JwtProvider jwtProvider;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("성공 - 유저의 칭호 목록 조회")
	@WithMockUser(roles = "ADMIN")
	void getUserTitles_success() throws Exception {
		Long userId = 1L;

		List<UserTitleResponse> mockResponse = List.of(
			new UserTitleResponse(10L, "모험가", "첫 칭호"),
			new UserTitleResponse(11L, "정복자", "많이 돌아다님")
		);

		given(titleService.getUserTitles(userId)).willReturn(mockResponse);

		mockMvc.perform(get("/api/titles/users/{userId}", userId))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 조회되었습니다."))
			   .andExpect(jsonPath("$.data").isArray())
			   .andExpect(jsonPath("$.data[0].id").value(10))
			   .andExpect(jsonPath("$.data[0].name").value("모험가"));
	}

	@Test
	@DisplayName("실패 - 존재하지 않는 유저의 칭호 목록 조회")
	@WithMockUser(roles = "ADMIN")
	void getUserTitles_fail_notFound() throws Exception {
		Long invalidUserId = 999L;

		given(titleService.getUserTitles(invalidUserId))
			.willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

		mockMvc.perform(get("/api/titles/users/{userId}", invalidUserId))
			   .andExpect(status().isNotFound())
			   .andExpect(jsonPath("$.success").value(false))
			   .andExpect(jsonPath("$.message").value("해당 유저를 찾을 수 없습니다."));
	}
}