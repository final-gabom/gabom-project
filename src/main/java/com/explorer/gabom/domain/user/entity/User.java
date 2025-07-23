package com.explorer.gabom.domain.user.entity;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.entity.BaseTimeEntity;
import com.explorer.gabom.global.file.entity.AttachmentFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE user SET status = 'INACTIVE', deleted_at = NOW() WHERE id = ?")
@Table(name = "user")
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(unique = true, nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole userRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_img_id")
	private AttachmentFile profileImg;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "title_id")
	private Title title;

	private String address;

	private Double lat;

	private Double lng;

	@Column(nullable = false)
	private Integer point;

	@Column(nullable = false)
	private Integer level;

	@Column(nullable = false)
	private Integer exp;

	@Builder
	public User(Long id, String email, String password, String nickname, UserRole userRole) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.userRole = userRole;
		this.status = UserStatus.ACTIVE;
		this.point = 0;
		this.level = 1;
		this.exp = 0;
	}

}
