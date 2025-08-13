package com.explorer.gabom.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.explorer.gabom.domain.address.entity.Address;
import com.explorer.gabom.domain.file.entity.AttachmentFile;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.domain.title.entity.UserTitle;
import com.explorer.gabom.domain.user.type.UserRole;
import com.explorer.gabom.domain.user.type.UserStatus;
import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE users SET status = 'INACTIVE', deleted_at = NOW() WHERE id = ?")
@Table(name = "users")
public class User extends BaseTimeEntity {

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<UserTitle> userTitles = new ArrayList<>();
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", insertable = false, updatable = false)
	private Address address;
	@Column(name = "address_id")
	private Long addressId;

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

	public void addPoint(int point) {
		this.point += point;
	}

	public void addExp(int exp) {
		this.exp += exp;
	}

	public void updateLevel(int newLevel) {
		this.level = newLevel;
	}

	public void addTitle(Title title) {
		boolean alreadyHas = userTitles.stream().anyMatch(userTitle -> userTitle.getTitle().equals(title));
		if (!alreadyHas) {
			userTitles.add(new UserTitle(this, title));
		}
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateProfileImg(AttachmentFile profileImg) {
		this.profileImg = profileImg;
	}

	public void setTitle(Title newTitle) {
		this.title = newTitle;
	}

	public void updatePassword(String encodedNewPassword) {
		this.password = encodedNewPassword;
	}

	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void updateAddressId(Long addressId) {
		this.addressId = addressId;
	}
}

