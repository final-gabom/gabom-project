package com.explorer.gabom.domain.user.service;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.repository.TitleRepository;
import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.PasswordUpdateRequest;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.dto.response.UpdateMainTitleResponse;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.file.repository.AttachmentFileRepository;
import com.explorer.gabom.global.validator.PasswordValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final AttachmentFileRepository fileRepository;
	private final TitleRepository titleRepository;
	private final PasswordValidator passwordValidator;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDto getUser(Long userId) {
		log.info("유저 상세 정보 조회 시작");
		User byIdAndStatus = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
										   .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
		return UserDto.toDto(byIdAndStatus);
	}

	@Override
	public UserDto updateUser(Long userId, UserUpdateRequest updateRequest) {
		String nickname = updateRequest.getNickname();
		String profileImgId = updateRequest.getProfileImgId();
		String address = updateRequest.getAddress();
		Double lat = updateRequest.getLat();
		Double lng = updateRequest.getLng();

		User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

		// 닉네임 변경 시 중복 검사
		if (nickname != null && !nickname.equals(user.getNickname())) {
			if (userRepository.existsByNickname(nickname)) {
				throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
			}
			user.updateNickname(nickname);
		}
		// 주소 변경
		if (address != null) {
			user.updateAddress(address);
		}
		if (lat != null) {
			user.updateLat(lat);
		}
		if (lng != null) {
			user.updateLng(lng);
		}
		// todo: 추후 파일 입출력 이후 변경 필요
		// 프로필 이미지 변경
		// if (profileImgId != null) {
		// 	AttachmentFile imgFile = fileRepository.findById(profileImgId)
		// 										   .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
		// 	user.updateProfileImg(imgFile);
		// }
		return UserDto.toDto(user);
	}

	@Override
	public void deleteUser(Long userId) {
		log.info("회원 탈퇴 시작");
		userRepository.deleteById(userId);
	}

	// 내 칭호변경
	@Override
	public UpdateMainTitleResponse updateMainTitle(Long userId, Long titleId) {
		Title title = titleRepository.findById(titleId)
									 .orElseThrow(() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));
		User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		user.setTitle(title);
		return new UpdateMainTitleResponse(title.getId(), title.getName());
	}

	@Override
	public void updatePassword(Long userId, PasswordUpdateRequest request) {
		User user = userRepository.findById(userId)
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		passwordValidator.verifyMatch(request.getOldPassword(), user.getPassword());

		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
		user.updatePassword(encodedNewPassword);
	}

}
