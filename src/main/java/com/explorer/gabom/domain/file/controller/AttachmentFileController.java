package com.explorer.gabom.domain.file.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.explorer.gabom.domain.file.dto.response.UploadFileResponse;
import com.explorer.gabom.domain.file.service.AttachmentFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class AttachmentFileController {

	private final AttachmentFileService attachmentFileService;

	@PostMapping("/image")
	public ResponseEntity<UploadFileResponse> uploadImageFile(@RequestParam MultipartFile file,
															  @RequestParam String fileType,
															  @RequestParam Long refId) throws IOException {
		return ResponseEntity.ok(this.attachmentFileService.uploadImageFile(file, fileType, refId));
	}
}
