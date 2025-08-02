package com.explorer.gabom.domain.exploration.controller;

import com.explorer.gabom.domain.exploration.dto.request.ExplorationStartRequest;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationExtendTimeResponse;
import com.explorer.gabom.domain.exploration.dto.response.ExplorationStartResponse;
import com.explorer.gabom.domain.exploration.service.ExplorationService;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExplorationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ExplorationService explorationService;

	@BeforeEach
	void setupSecurityContext() {
		// CustomUserDetails 직접 생성
		CustomUserDetails userDetails = CustomUserDetails.builder()
														 .userId(1L)
														 .email("test@example.com")
														 .password("password")
														 .role(UserRole.USER)
														 .user(null) // 필요하면 실제 User 엔티티 넣어도 됨
														 .build();

		// Authentication 객체 생성 후 SecurityContext에 세팅
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	// 탐험 시작
	@Test
	@DisplayName("탐험 시작 - 성공")
	void startExploration_success() throws Exception {
		ExplorationStartRequest request = new ExplorationStartRequest();

		Field latField = ExplorationStartRequest.class.getDeclaredField("lat");
		latField.setAccessible(true);
		latField.set(request, 37.123456);

		Field lngField = ExplorationStartRequest.class.getDeclaredField("lng");
		lngField.setAccessible(true);
		lngField.set(request, 127.123456);

		ExplorationStartResponse response = ExplorationStartResponse.builder()
																	.explorationId(1L)
																	.rewardPoint(50)
																	.rewardExp(30)
																	.startAt(LocalDateTime.now())
																	.endAt(LocalDateTime.now().plusHours(3))
																	.build();

		when(explorationService.startExploration(anyLong(), anyLong(), any(ExplorationStartRequest.class)))
			.thenReturn(response);

		mockMvc.perform(post("/api/exploration/1/start")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("탐험이 시작되었습니다."))
			   .andExpect(jsonPath("$.data.explorationId").value(1))
			   .andExpect(jsonPath("$.data.rewardPoint").value(50))
			   .andExpect(jsonPath("$.data.rewardExp").value(30))
			   .andExpect(jsonPath("$.data.startAt").exists())
			   .andExpect(jsonPath("$.data.endAt").exists());
	}

	// 탐험 제한 시간 연장
	@Test
	@DisplayName("탐험 제한 시간 연장 - 성공")
	void extendExplorationTime_success() throws Exception {
		LocalDateTime newDeadline = LocalDateTime.now().plusHours(3);
		ExplorationExtendTimeResponse response = new ExplorationExtendTimeResponse(1L, newDeadline);

		when(explorationService.extendExplorationTime(anyLong(), anyLong())).thenReturn(response);

		mockMvc.perform(patch("/api/exploration/1/extend-time")
							.contentType(MediaType.APPLICATION_JSON))
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.success").value(true))
			   .andExpect(jsonPath("$.message").value("탐험 제한 시간이 연장되었습니다."))
			   .andExpect(jsonPath("$.data.explorationId").value(1))
			   .andExpect(jsonPath("$.data.newDeadline").exists());
	}
}