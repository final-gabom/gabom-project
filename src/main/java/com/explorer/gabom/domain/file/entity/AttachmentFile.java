package com.explorer.gabom.domain.file.entity;

import org.hibernate.annotations.UuidGenerator;

import com.explorer.gabom.domain.file.type.FileType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "attachment_file")
@NoArgsConstructor
@Builder
@AllArgsConstructor
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
	private Long fileSize;

	@Column(nullable = false)
	private Long refId;

	@Column
	private String filePath;

	@Column(nullable = false)
	private String mimeType;

	@Column(nullable = false)
	private String hash;

	private boolean deleted;

	private int orderIdx;  // 참조 게시글 내 순서

	@Builder
	public AttachmentFile(FileType fileType, String fileName, Long fileSize, Long refId, String mimeType, String hash,
						  String filePath, int orderIdx) {
		this.fileType = fileType;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.refId = refId;
		this.mimeType = mimeType;
		this.hash = hash;
		this.filePath = filePath;
		this.orderIdx = orderIdx;
	}

}
