package com.explorer.gabom.domain.missionproof.entity;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.place.entity.Place;
import com.explorer.gabom.global.entity.BaseTimeEntity;
import com.explorer.gabom.domain.file.entity.AttachmentFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mission_proof")
@SQLDelete(sql = "UPDATE mission_proof SET deleted_at = NOW() WHERE id = ?")
public class MissionProof extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@Column(nullable = false)
	private String title;

	@Lob
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attachment_img_id", nullable = false)
	private AttachmentFile attachmentImg;

}
