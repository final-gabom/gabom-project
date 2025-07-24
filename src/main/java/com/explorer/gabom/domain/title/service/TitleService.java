package com.explorer.gabom.domain.title.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
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


}
