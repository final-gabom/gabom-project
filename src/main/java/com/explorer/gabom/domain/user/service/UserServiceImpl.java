package com.explorer.gabom.domain.user.service;

import org.springframework.stereotype.Service;

import com.explorer.gabom.domain.user.dto.UserDto;
import com.explorer.gabom.domain.user.dto.request.UserUpdateRequest;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.domain.user.repository.UserRepository;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;
import com.explorer.gabom.global.file.repository.AttachmentFileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final AttachmentFileRepository fileRepository;

	@Override
	public UserDto getUser(Long userId) {
		log.info("유저 상세 정보 조회 시작");
		User byIdAndStatus = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
										   .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
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
								  .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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

}
