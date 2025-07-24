package com.explorer.gabom.domain.title.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleUpdateResponse;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.global.exception.BusinessException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TitleService {
	private final TitleRepository titleRepository;

	public TitleCreateResponse createTitle(TitleCreateRequest request) {
		if (titleRepository.existsByName(request.getName())) {
			throw new BusinessException(ErrorCode.TITLE_DUPLICATED);
		}

		Title title = new Title(request.getName(), request.getDescription());
		Title saved = titleRepository.save(title);
		return TitleCreateResponse.from(saved);
	}

	public TitleUpdateResponse updateTitle(Long titleId, TitleUpdateRequest request) {
		Title title = titleRepository.findById(titleId)
									 .orElseThrow(() -> new BusinessException(ErrorCode.TITLE_NOT_FOUND));

		title.update(request.getName(), request.getDescription());
		return TitleUpdateResponse.from(title);
	}

	public LocalDateTime deleteTitle(Long titleId) {
		Title title = titleRepository.findById(titleId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TITLE_NOT_FOUND));

		titleRepository.delete(title);
		return LocalDateTime.now();
	}


}
