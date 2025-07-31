package com.explorer.gabom.domain.file.dto;

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
}
