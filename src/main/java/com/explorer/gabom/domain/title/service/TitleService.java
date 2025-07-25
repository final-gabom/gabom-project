package com.explorer.gabom.domain.title.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleUpdateResponse;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TitleService {
	private final TitleRepository titleRepository;

	public TitleCreateResponse createTitle(TitleCreateRequest request) {
		log.info("<칭호등록> 요청 - name: {}, description: {}", request.getName(), request.getDescription());
		if (titleRepository.existsByName(request.getName())) {
			log.warn("<칭호등록> 실패 - 중복된 이름: {}", request.getName());
			throw new CustomException(ErrorCode.TITLE_ALREADY_EXISTS);
		}

		Title title = new Title(request.getName(), request.getDescription());
		Title saved = titleRepository.save(title);

		log.info("<칭호등록> 성공 - 등록된 ID: {}", saved.getId());
		return TitleCreateResponse.toDto(saved);
	}

	public TitleUpdateResponse updateTitle(Long titleId, TitleUpdateRequest request) {
		log.info("<칭호수정> 요청 - ID: {}, name: {}, description: {}", titleId, request.getName(), request.getDescription());
		Title title = titleRepository.findById(titleId)
									 .orElseThrow(() -> {
										 log.warn("<칭호수정> 실패 - 존재하지 않는 ID: {}", titleId);
										 return new CustomException(ErrorCode.TITLE_NOT_FOUND);
									 });

		title.update(request.getName(), request.getDescription());

		log.info("<칭호수정> 성공 - 수정된 ID: {}", titleId);
		return TitleUpdateResponse.toDto(title);
	}

}
