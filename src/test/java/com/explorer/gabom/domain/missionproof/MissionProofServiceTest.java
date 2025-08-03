package com.explorer.gabom.domain.missionproof;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.file.type.FileType;
import com.explorer.gabom.domain.missionproof.dto.request.CreateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.request.UpdateMissionProofRequest;
import com.explorer.gabom.domain.missionproof.dto.response.CreateMissionProofResponse;
import com.explorer.gabom.domain.missionproof.entity.MissionProof;
import com.explorer.gabom.domain.missionproof.repository.MissionProofRepository;

import com.explorer.gabom.domain.missionproof.service.MissionProofServiceImpl;
import com.explorer.gabom.domain.missionproof.type.MissionProofType;
import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.domain.place.entity.PlaceStatus;
import com.explorer.gabom.domain.place.repository.PlaceRepository;
import com.explorer.gabom.domain.user.entity.User;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class MissionProofServiceTest {

	@Mock
	private MissionProofRepository missionProofRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private AttachmentFileRepository attachmentFileRepository;

	@InjectMocks
	private MissionProofServiceImpl missionProofService;

	private User mockUser;
	private Place mockPlace;

	@BeforeEach
	void setup() {
		mockUser = User.builder().id(1L).nickname("testUser").build();

		mockPlace = Place.builder()
						 .id(100L)
						 .title("테스트 장소")  // ✅ 올바른 필드명
						 .address("서울시 강남구 어딘가")  // 필수 필드
						 .lat(37.1234)                   // 필수 필드
						 .lng(127.5678)                  // 필수 필드
						 .content("설명")
						 .proofMethod("사진 인증")
						 .viewCount(0)
						 .status(PlaceStatus.APPROVED)
						 .user(mockUser)
						 .build();
	}

	@Test
	@DisplayName("미션 인증글 생성 성공")
	void createMissionProof_success() {
		// given
		CreateMissionProofRequest request = CreateMissionProofRequest.builder()
																	 .fieldType(MissionProofType.PLACE)
																	 .targetId(mockPlace.getId())
																	 .data(
																		 CreateMissionProofRequest.Data.builder()
																									   .title("인증 제목")
																									   .content("인증 내용")
																									   .imageId(
																										   Collections.emptyList())
																									   .starRating(4)
																									   .build())
																	 .build();

		when(placeRepository.findById(mockPlace.getId())).thenReturn(Optional.of(mockPlace));
		when(missionProofRepository.save(any(MissionProof.class))).thenAnswer(invocation -> {
			MissionProof saved = invocation.getArgument(0);
			saved.setId(1L); // id 설정
			return saved;
		});

		// when
		CreateMissionProofResponse response = missionProofService.createMissionProof(request, mockUser);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getTitle()).isEqualTo("인증 제목");
		assertThat(response.getContent()).isEqualTo("인증 내용");
		verify(missionProofRepository).save(any(MissionProof.class));
	}

	@Test
	@DisplayName("미션 인증글 생성 실패 - 장소 없음")
	void createMissionProof_fail_placeNotFound() {
		// given
		CreateMissionProofRequest request = CreateMissionProofRequest.builder()
																	 .fieldType(MissionProofType.PLACE)
																	 .targetId(999L)
																	 .data(
																		 CreateMissionProofRequest.Data.builder()
																									   .title("인증 제목")
																									   .content("인증 내용")
																									   .imageId(
																										   Collections.emptyList())
																									   .starRating(4)
																									   .build())
																	 .build();

		when(placeRepository.findById(999L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> missionProofService.createMissionProof(request, mockUser)).isInstanceOf(
			CustomException.class).hasMessageContaining(ErrorCode.PLACE_NOT_FOUND.getMessage());
	}


	@Test
	@DisplayName("미션 인증글 수정 성공")
	void updateMissionProof_success() {
		// given
		Long missionProofId = 1L;
		Long userId = 1L;

		MissionProof existing = MissionProof.builder()
											.id(missionProofId)
											.user(mockUser)
											.title("Old Title")
											.content("Old Content")
											.imageFiles(new ArrayList<>())
											.fieldType(MissionProofType.PLACE)
											.targetId(100L)
											.place(mockPlace)
											.starRating(4)
											.build();

		UpdateMissionProofRequest request = UpdateMissionProofRequest.builder()
																	 .title("New Title")
																	 .content("New Content")
																	 .imgFileIds(List.of("file1", "file2"))
																	 .build();

		List<AttachmentFile> mockFiles = List.of(
			AttachmentFile.builder().filePath("path1")
						  .fileType(FileType.MISSION_PROOF)
						  .build(),
			AttachmentFile.builder().filePath("path2")
						  .fileType(FileType.MISSION_PROOF)
						  .build()
		);

		when(missionProofRepository.findById(missionProofId)).thenReturn(Optional.of(existing));
		when(attachmentFileRepository.findAllByFileIdIn(request.getImgFileIds())).thenReturn(mockFiles);

		// when
		CreateMissionProofResponse response = missionProofService.updateMissionProof(missionProofId, request, userId);

		// then
		assertThat(response.getTitle()).isEqualTo("New Title");
		assertThat(response.getContent()).isEqualTo("New Content");
		List<FileResponseDto> files = response.getProfileImages();
		assertThat(files).hasSize(2);
		verify(missionProofRepository).findById(missionProofId);
		verify(attachmentFileRepository).findAllByFileIdIn(request.getImgFileIds());
	}

	@Test
	@DisplayName("미션 인증글 수정 실패 - 인증글 없음")
	void updateMissionProof_fail_notFound() {
		Long id = 99L;
		when(missionProofRepository.findById(id)).thenReturn(Optional.empty());

		UpdateMissionProofRequest request = UpdateMissionProofRequest.builder()
																	 .title("제목")
																	 .content("내용")
																	 .imgFileIds(List.of())
																	 .build();

		assertThatThrownBy(() -> missionProofService.updateMissionProof(id, request, 1L))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_MISSION_PROOF.getMessage());
	}

	@Test
	@DisplayName("미션 인증글 수정 실패 - 작성자 불일치")
	void updateMissionProof_fail_forbidden() {
		Long id = 1L;
		Long 다른유저ID = 2L;

		MissionProof existing = MissionProof.builder()
											.id(id)
											.user(mockUser) // userId = 1
											.build();

		when(missionProofRepository.findById(id)).thenReturn(Optional.of(existing));

		UpdateMissionProofRequest request = UpdateMissionProofRequest.builder()
																	 .title("제목")
																	 .content("내용")
																	 .imgFileIds(List.of())
																	 .build();

		assertThatThrownBy(() -> missionProofService.updateMissionProof(id, request, 2L))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.FORBIDDEN_UPDATE_MISSION_PROOF.getMessage());
	}
}

