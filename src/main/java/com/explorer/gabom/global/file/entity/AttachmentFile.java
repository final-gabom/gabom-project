package com.explorer.gabom.global.file.entity;

import org.hibernate.annotations.UuidGenerator;

import com.explorer.gabom.global.file.type.FileType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "attachment_file")
public class AttachmentFile {

	@Id
	@UuidGenerator
	private String fileId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FileType fileType;

	@Column(nullable = false)
	private String fileName;

	@Column(nullable = false)
	private String fileUrl;

	@Column(nullable = false)
	private Long fileSize;

	@Column(nullable = false)
	private Long refId;

	@Column(nullable = false)
	private String mimeType;

	private int orderIdx;  // 참조 게시글 내 순서
}
