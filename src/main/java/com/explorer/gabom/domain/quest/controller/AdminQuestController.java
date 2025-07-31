package com.explorer.gabom.domain.quest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequest;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequest;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestDeleteResponse;
import com.explorer.gabom.domain.quest.dto.response.QuestUpdateResponse;
import com.explorer.gabom.domain.quest.service.AdminQuestService;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/quests")
@RequiredArgsConstructor
public class AdminQuestController {

	private final AdminQuestService adminQuestService;

	@PostMapping
	public ResponseEntity<ApiResponse<QuestCreateResponse>> createQuest(
		@Valid @RequestBody QuestCreateRequest request) {

		QuestCreateResponse response = adminQuestService.createQuest(request);
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(ApiResponse.success("퀘스트가 성공적으로 등록되었습니다.", response));
	}

	@PatchMapping("/{questId}")
	public ResponseEntity<ApiResponse<QuestUpdateResponse>> updateQuest(
		@PathVariable Long questId,
		@Valid @RequestBody QuestUpdateRequest request) {

		QuestUpdateResponse response = adminQuestService.updateQuest(questId, request);
		return ResponseEntity.status(HttpStatus.OK)
							 .body(ApiResponse.success("퀘스트가 성공적으로 수정되었습니다.", response));
	}

	@DeleteMapping("/{questId}")
	public ResponseEntity<ApiResponse<QuestDeleteResponse>> deleteQuest(@PathVariable Long questId) {

		QuestDeleteResponse response = adminQuestService.deleteQuest(questId);
		return ResponseEntity.status(HttpStatus.OK)
							 .body(ApiResponse.success("퀘스트가 삭제되었습니다.", response));
	}
}
