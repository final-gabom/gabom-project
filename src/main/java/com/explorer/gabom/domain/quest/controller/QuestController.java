package com.explorer.gabom.domain.quest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.quest.dto.request.QuestCreateRequestDto;
import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequestDto;
import com.explorer.gabom.domain.quest.dto.response.QuestCreateResponseDto;
import com.explorer.gabom.domain.quest.dto.response.QuestUpdateResponseDto;
import com.explorer.gabom.domain.quest.service.QuestService;
import com.explorer.gabom.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestController {

	private final QuestService questService;

	@PostMapping("/admin/quests")
	public ResponseEntity<ApiResponse<QuestCreateResponseDto>> createQuest(
		@Valid @RequestBody QuestCreateRequestDto request) {

		QuestCreateResponseDto response = questService.createQuest(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("퀘스트가 성공적으로 등록되었습니다.", response));
	}

	@PatchMapping("admin/quests/{questId}")
	public ResponseEntity<ApiResponse<QuestUpdateResponseDto>> updateQuest(
		@PathVariable Long questId,
		@Valid @RequestBody QuestUpdateRequestDto request) {

		QuestUpdateResponseDto response = questService.updateQuest(questId, request);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("퀘스트가 성공적으로 수정되었습니다.", response));
	}
}
