package com.explorer.gabom.domain.place.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.explorer.gabom.domain.file.dto.FileResponseDto;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetailResponseDto {

	private Long id;
	private String title;
	private String address;
	private Double lat;
	private Double lng;
	private Integer missionProofCount;
	private Double avgScore;
	private String content;       // 본문 내용
	private String proofMethod;
	private Integer viewCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private UserSummaryDto writer;
	private List<FileResponseDto> files;



}
