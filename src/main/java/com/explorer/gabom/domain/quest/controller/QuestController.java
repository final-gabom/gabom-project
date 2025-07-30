package com.explorer.gabom.domain.quest.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.explorer.gabom.domain.quest.dto.QuestDto;
import com.explorer.gabom.domain.quest.service.QuestService;
import com.explorer.gabom.global.dto.ApiResponse;
import com.explorer.gabom.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

	private final QuestService questService;

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<QuestDto>>> getQuestPage(
		@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		PageResponse<QuestDto> response = questService.getQuestPage(pageable);
		return ResponseEntity.status(HttpStatus.OK)
							 .body(ApiResponse.success("퀘스트 리스트 조회를 성공했습니다.", response));
	}

	@GetMapping("/{questId}")
	public ResponseEntity<ApiResponse<QuestDto>> getQuestById(
		@PathVariable Long questId
	) {
		QuestDto response = questService.getQuestById(questId);
		return ResponseEntity.status(HttpStatus.OK)
							 .body(ApiResponse.success("퀘스트 조회를 성공했습니다.", response));
	}

}
