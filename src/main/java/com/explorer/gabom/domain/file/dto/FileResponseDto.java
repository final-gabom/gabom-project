package com.explorer.gabom.domain.file.dto;

import com.explorer.gabom.domain.file.entity.AttachmentFile;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileResponseDto {
	private final String fileId;
	private final String fileType;
	private final Long refId;
	private final String fileName;
	private final String mimeType;
	private final Long fileSize;
	private final String filePath;

	public static FileResponseDto toDto(AttachmentFile file) {
		return FileResponseDto.builder()
							  .fileId(file.getFileId())
							  .fileType(file.getFileType().name())
							  .refId(file.getRefId())
							  .fileName(file.getFileName())
							  .mimeType(file.getMimeType())
							  .filePath(file.getFilePath())
							  .fileSize(file.getFileSize())
							  .build();
	}
}
