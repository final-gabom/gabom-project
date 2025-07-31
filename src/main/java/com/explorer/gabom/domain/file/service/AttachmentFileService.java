package com.explorer.gabom.domain.file.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.explorer.gabom.domain.file.dto.response.UploadFileResponse;
import com.explorer.gabom.domain.file.type.FileType;

public interface AttachmentFileService {

	UploadFileResponse uploadImageFile(MultipartFile file, FileType fileType, Long refId) throws IOException;

}
