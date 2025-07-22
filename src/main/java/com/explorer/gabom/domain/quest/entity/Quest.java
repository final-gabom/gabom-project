package com.explorer.gabom.domain.quest.entity;

import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quest")
public class Quest extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private Integer acquiredCondition;

	private Integer rewardPoint;
	private Integer rewardExp;

	@ManyToOne
	@JoinColumn(name = "title_id")
	private Title title;
}
