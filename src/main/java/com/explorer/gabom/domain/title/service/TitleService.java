package com.explorer.gabom.domain.title.service;

  import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.activity.aop.ActivityLoggable;
import com.explorer.gabom.domain.activity.type.ActivityType;
import com.explorer.gabom.domain.title.dto.request.TitleCreateRequest;
import com.explorer.gabom.domain.title.dto.request.TitleUpdateRequest;
import com.explorer.gabom.domain.title.dto.response.TitleCreateResponse;
import com.explorer.gabom.domain.title.dto.response.TitleDeleteResponse;
import com.explorer.gabom.domain.title.dto.response.TitleUpdateResponse;
import com.explorer.gabom.domain.title.dto.response.UserTitleResponse;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.entity.UserTitle;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TitleService {
	private final TitleRepository titleRepository;
	private final UserRepository userRepository;

	@ActivityLoggable(ActivityType.ADMIN_TITLE_CREATED)
	public TitleCreateResponse createTitle(TitleCreateRequest request) {
		log.info("<칭호등록> 요청 - name: {}, description: {}", request.getName(), request.getDescription());
		if (titleRepository.existsByName(request.getName())) {
			throw new CustomException(ErrorCode.TITLE_ALREADY_EXISTS);
		}

		Title title = new Title(request.getName(), request.getDescription());
		Title saved = titleRepository.save(title);

		log.info("<칭호등록> 성공 - 등록된 ID: {}", saved.getId());
		return TitleCreateResponse.toDto(saved);
	}

	@Transactional
	@ActivityLoggable(ActivityType.ADMIN_TITLE_UPDATED)
	public TitleUpdateResponse updateTitle(Long titleId, TitleUpdateRequest request) {
		log.info("<칭호수정> 요청 - ID: {}, name: {}, description: {}", titleId, request.getName(), request.getDescription());
		Title title = titleRepository.findById(titleId)
									 .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));

		titleRepository.updateTitle(titleId, request.getName(), request.getDescription());

		log.info("<칭호수정> 성공 - 수정된 ID: {}", titleId);
		return TitleUpdateResponse.toDto(title);
	}

	@ActivityLoggable(ActivityType.ADMIN_TITLE_DELETED)
	public TitleDeleteResponse deleteTitle(Long titleId) {
		log.info("<칭호삭제> 요청 - ID: {}", titleId);
		Title title = titleRepository.findById(titleId)
									 .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));

		LocalDateTime deletedAt = LocalDateTime.now();

		TitleDeleteResponse response = TitleDeleteResponse.toDto(title, deletedAt);

		titleRepository.delete(title);
		log.info("<칭호삭제> 성공 - 삭제된 ID: {}", titleId);
		return response;
	}

	@Transactional(readOnly = true)
	public List<UserTitleResponse> getUserTitles(Long userId) {
		log.info("<칭호조회> 요청 - userId: {}", userId);
		User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<UserTitle> userTitles = user.getUserTitles();
		log.info("<칭호조회> 성공 - 조회된 ID: {}", userId);
		return userTitles.stream()
						 .map(UserTitleResponse::toDto)
						 .collect(Collectors.toList());
	}


}
