package com.explorer.gabom.domain.quest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.quest.dto.response.QuestRewardResponse;
import com.explorer.gabom.domain.quest.service.UserQuestService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-quests")
@RequiredArgsConstructor
public class UserQuestController {

	private final UserQuestService userQuestService;

	@PostMapping("/{userQuestId}/reward")
	public ResponseEntity<ApiResponse<QuestRewardResponse>> claimReward(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long userQuestId
	) {
		QuestRewardResponse response = userQuestService.claimReward(userDetails.getUserId(), userQuestId);
		return ResponseEntity.status(HttpStatus.OK)
							 .body(ApiResponse.success("퀘스트 보상이 지급되었습니다.", response));
	}
}
