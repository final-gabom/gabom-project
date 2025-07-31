package com.explorer.gabom.domain.file.dto;

import com.explorer.gabom.domain.file.entity.AttachmentFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailDto {
	private String fileId;
	private String filePath;

	public static ThumbnailDto toDto(AttachmentFile firstFile) {
		return ThumbnailDto.builder()
						   .fileId(firstFile.getFileId())
						   .filePath(firstFile.getFilePath())
						   .build();
	}
}
