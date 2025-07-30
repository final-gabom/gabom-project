package com.explorer.gabom.domain.file.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.explorer.gabom.domain.file.dto.response.UploadFileResponse;
import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.repository.AttachmentFileRepository;
import com.explorer.gabom.domain.file.storage.FileStorageService;
import com.explorer.gabom.domain.file.type.FileType;
import com.explorer.gabom.domain.file.util.MimeUtil;
import com.explorer.gabom.global.exception.CustomException;
import com.explorer.gabom.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentFileServiceImpl implements AttachmentFileService {

	private final FileStorageService fileStorageService;
	private final AttachmentFileRepository attachmentFileRepository;

	@Override
	@Transactional
	public UploadFileResponse uploadImageFile(MultipartFile file, FileType fileType, Long refId) throws IOException {
		String hash = this.fileStorageService.uploadImage(file);

		String mimeType = MimeUtil.getMimeType(file.getOriginalFilename());
		if (mimeType == null || !mimeType.startsWith("image/")) {
			throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
		}

		AttachmentFile attachmentFile = AttachmentFile.builder()
													  .fileType(fileType)
													  .fileName(file.getOriginalFilename())
													  .fileSize(file.getSize())
													  .refId(refId)
													  .mimeType(mimeType)
													  .hash(hash)
													  .filePath(fileStorageService.getTargetPath(hash))
													  .build();

		AttachmentFile savedFile = this.attachmentFileRepository.save(attachmentFile);
		System.out.println("savedFilePath: " + savedFile.getFilePath());

		return UploadFileResponse.toDto(savedFile);
	}

}
