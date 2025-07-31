package com.explorer.gabom.domain.file.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.file.type.FileType;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, String> {
	List<AttachmentFile> findByFileTypeAndRefIdAndDeletedFalse(FileType fileType, Long refId);

	List<AttachmentFile> findAllByFilePathIn(List<String> filePaths);


}
