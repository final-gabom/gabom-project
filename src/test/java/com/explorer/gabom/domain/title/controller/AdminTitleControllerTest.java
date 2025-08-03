package com.explorer.gabom.domain.title.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleDeleteResponse;
import com.explorer.gabom.domain.title.service.TitleService;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@DisplayName("AdminTitleController - 통합 테스트")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class AdminTitleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TitleService titleService;

	@MockBean
	private JwtProvider jwtProvider;

	@Autowired
	private ObjectMapper objectMapper;

	@Nested
	@DisplayName("칭호 등록 API [POST /api/admin/titles]")
	class CreateTitle {

		@Test
		@DisplayName("성공 - 올바른 요청 시 201 상태코드와 데이터 반환")
		@WithMockUser(roles = "ADMIN")
		void createTitle_success() throws Exception {
			// Given
			TitleCreateRequest request = new TitleCreateRequest("차도남/녀", "서울 10곳 이상 탐험");
			TitleCreateResponse response = new TitleCreateResponse(1L, "차도남/녀", "서울 10곳 이상 탐험", LocalDateTime.now());

			given(titleService.createTitle(any(TitleCreateRequest.class)))
				.willReturn(response);

			// When & Then
			mockMvc.perform(post("/api/admin/titles")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
				   .andExpect(status().isCreated())
				   .andExpect(jsonPath("$.success").value(true))
				   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 등록되었습니다."))
				   .andExpect(jsonPath("$.data.name").value("차도남/녀"));
		}

		@Test
		@DisplayName("실패 - 이름이 비어 있을 경우 400 반환")
		@WithMockUser(roles = "ADMIN")
		void createTitle_fail_validation() throws Exception {
			// Given
			TitleCreateRequest request = new TitleCreateRequest("", "없음");

			// When & Then
			mockMvc.perform(post("/api/admin/titles")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
				   .andExpect(status().isBadRequest())
				   .andExpect(jsonPath("$.success").value(false))
				   .andExpect(jsonPath("$.message").exists());
		}
	}

	@Nested
	@DisplayName("칭호 수정 API [PATCH /api/admin/titles/{id}]")
	class UpdateTitle {

		@Test
		@DisplayName("성공 - 칭호가 정상적으로 수정됨")
		@WithMockUser(roles = "ADMIN")
		void updateTitle_success() throws Exception {
			// Given
			TitleUpdateRequest request = new TitleUpdateRequest("응애 탐험가", "누적 탐험 10km");

			// When & Then
			mockMvc.perform(patch("/api/admin/titles/{id}", 1L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
				   .andExpect(status().isOk())
				   .andExpect(jsonPath("$.success").value(true))
				   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 수정되었습니다."));

			then(titleService).should().updateTitle(eq(1L), any(TitleUpdateRequest.class));
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 ID일 경우 404 예외")
		@WithMockUser(roles = "ADMIN")
		void updateTitle_fail_notFound() throws Exception {
			// Given
			TitleUpdateRequest request = new TitleUpdateRequest("전설", "전설적인 자");

			doThrow(new CustomException(ErrorCode.TITLE_NOT_FOUND))
				.when(titleService).updateTitle(eq(999L), any());

			// When & Then
			mockMvc.perform(patch("/api/admin/titles/{id}", 999L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
				   .andExpect(status().isNotFound())
				   .andExpect(jsonPath("$.success").value(false))
				   .andExpect(jsonPath("$.message").value("해당 칭호를 찾을 수 없습니다."));
		}
	}

	@Nested
	@DisplayName("칭호 삭제 API [DELETE /api/admin/titles/{id}]")
	class DeleteTitle {

		@Test
		@DisplayName("성공 - 칭호가 정상적으로 삭제됨")
		@WithMockUser(roles = "ADMIN")
		void deleteTitle_success() throws Exception {
			// Given
			TitleDeleteResponse response = new TitleDeleteResponse(1L);

			given(titleService.deleteTitle(1L)).willReturn(response);

			// When & Then
			mockMvc.perform(delete("/api/admin/titles/{id}", 1L))
				   .andExpect(status().isOk())
				   .andExpect(jsonPath("$.success").value(true))
				   .andExpect(jsonPath("$.message").value("칭호가 성공적으로 삭제되었습니다."))
				   .andExpect(jsonPath("$.data.id").value(1L));
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 ID일 경우 404 예외")
		@WithMockUser(roles = "ADMIN")
		void deleteTitle_fail_notFound() throws Exception {
			// Given
			given(titleService.deleteTitle(999L))
				.willThrow(new CustomException(ErrorCode.TITLE_NOT_FOUND));

			// When & Then
			mockMvc.perform(delete("/api/admin/titles/{id}", 999L))
				   .andExpect(status().isNotFound())
				   .andExpect(jsonPath("$.success").value(false))
				   .andExpect(jsonPath("$.message").value("해당 칭호를 찾을 수 없습니다."));
		}
	}
}