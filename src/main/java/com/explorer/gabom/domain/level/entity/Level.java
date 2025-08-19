package com.explorer.gabom.domain.level.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "level")
public class Level {

	@Id
	private int level;

	@Column(nullable = false)
	private Long requiredExp;
}
