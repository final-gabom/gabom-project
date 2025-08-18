package com.explorer.gabom.domain.user.service;

import static com.explorer.gabom.global.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.explorer.gabom.domain.address.dto.AddressDto;
import com.explorer.gabom.domain.address.dto.request.AddressRequest;
import com.explorer.gabom.domain.address.service.AddressService;
import com.explorer.gabom.domain.address.type.AddressType;
import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
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
import com.explorer.gabom.global.validator.PasswordValidator;

import jakarta.validation.constraints.Email;
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
	private final AddressService addressService;

	@Transactional(readOnly = true)
	public UserDto getUser(User user) {
		return UserDto.toDto(user);
	}

	@Transactional(readOnly = true)
	@Override
	public UserDto getUser(Long userId) {
		log.info("유저 상세 정보 조회 시작");
		User byIdAndStatus = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE).orElseThrow(
			() -> new CustomException(USER_NOT_FOUND));
		return UserDto.toDto(byIdAndStatus);
	}

	@Transactional
	@Override
	public UserDto updateUser(User user, UserUpdateRequest updateRequest) {
		String nickname = updateRequest.getNickname();
		String profileImgId = updateRequest.getProfileImgId();

		// 닉네임 변경 시 중복 검사
		if (nickname != null && !nickname.equals(user.getNickname())) {
			if (userRepository.existsByNickname(nickname)) {
				throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
			}
			user.updateNickname(nickname);
		}

		// 프로필 이미지 변경
		if (profileImgId != null) {
			AttachmentFile imgFile = fileRepository.findById(profileImgId).orElseThrow(
				() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
			user.updateProfileImg(imgFile);
		}

		return UserDto.toDto(user);
	}

	@Transactional
	@Override
	public void deleteUser(User user) {
		log.info("회원 탈퇴 시작: userId={}", user.getId());
		userRepository.delete(user);
	}

	// 내 칭호변경
	@Transactional
	@Override
	public UpdateMainTitleResponse updateMainTitle(User user, Long titleId) {
		Title title = titleRepository.findById(titleId).orElseThrow(
			() -> new CustomException(ErrorCode.TITLE_NOT_FOUND));
		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
		user.setTitle(title);
		return new UpdateMainTitleResponse(title.getId(), title.getName());
	}

	@Transactional
	@Override
	public void updatePassword(User user, PasswordUpdateRequest request) {
		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		passwordValidator.verifyMatch(request.getOldPassword(), user.getPassword());

		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
		user.updatePassword(encodedNewPassword);
	}

	@Transactional
	@Override
	public AddressDto updateUserAddress(User user, AddressRequest request) {
		request.setAddressTypeCd(AddressType.USER);
		request.setTargetId(user.getId());

		return addressService.createOrReplace(request);
	}

	@Override
	public void validateEmailNotExists(@Email String email) {
		if (userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE).isPresent()) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}

	@Override
	public void validateNicknameNotExists(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
	}
}
