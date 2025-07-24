package com.explorer.gabom.domain.title.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleResponse;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.global.exception.BusinessException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TitleService {
	private final TitleRepository titleRepository;

	public TitleResponse createTitle(TitleCreateRequest request) {
		if (titleRepository.existsByName(request.name())) {
			throw new BusinessException(ErrorCode.TITLE_DUPLICATED);
		}

		Title title = new Title(request.name(), request.description());
		Title saved = titleRepository.save(title);
		return TitleResponse.from(saved);
	}

	public TitleResponse updateTitle(Long titleId, TitleUpdateRequest request) {
		Title title = titleRepository.findById(titleId)
									 .orElseThrow(() -> new BusinessException(ErrorCode.TITLE_NOT_FOUND));

		title.update(request.getName(), request.getDescription());
		return TitleResponse.from(title);
	}

	public LocalDateTime deleteTitle(Long titleId) {
		Title title = titleRepository.findById(titleId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TITLE_NOT_FOUND));

		titleRepository.delete(title);
		return LocalDateTime.now();
	}


}
