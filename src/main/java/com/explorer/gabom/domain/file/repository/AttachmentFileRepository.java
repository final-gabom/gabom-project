package com.explorer.gabom.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.file.entity.AttachmentFile;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, String> {
}
