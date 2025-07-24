package com.explorer.gabom.global.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.global.file.entity.AttachmentFile;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, String> {
}
