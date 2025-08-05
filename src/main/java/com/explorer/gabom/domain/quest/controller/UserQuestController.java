package com.explorer.gabom.domain.quest.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.quest.dto.UserQuestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestRewardResponse;
import com.explorer.gabom.domain.quest.service.UserQuestService;
import com.explorer.gabom.domain.quest.type.ProgressStatus;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.PageResponse;
import com.explorer.gabom.global.security.userdetails.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-quests")
@RequiredArgsConstructor
public class UserQuestController implements UserQuestControllerDocs {

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

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<UserQuestDto>>> getProgress(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
		@RequestParam(value = "status", required = false) ProgressStatus progressStatus
	) {
		PageResponse<UserQuestDto> response = userQuestService.getProgress(
			userDetails.getUserId(), progressStatus, pageable);

		return ResponseEntity.ok(
			ApiResponse.success("유저 퀘스트 조회에 성공했습니다.", response)
		);
	}
}
