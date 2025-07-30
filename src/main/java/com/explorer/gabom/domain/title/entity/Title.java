package com.explorer.gabom.domain.title.entity;

import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "title")
@Getter
public class Title extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String description;

	public Title(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getValue() {
		return this.name;
	}
}
