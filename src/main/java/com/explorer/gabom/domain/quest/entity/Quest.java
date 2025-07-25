package com.explorer.gabom.domain.quest.entity;

import com.explorer.gabom.domain.quest.dto.request.QuestUpdateRequestDto;
import com.explorer.gabom.domain.quest.type.QuestConditionType;
import com.explorer.gabom.domain.title.entity.Title;
import com.explorer.gabom.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quest")
@Getter
@NoArgsConstructor
public class Quest extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private QuestConditionType questConditionType;

	@Column(name = "acquired_condition", nullable = false)
	private int acquireCondition;

	@Column(name = "reward_point", nullable = false)
	private int rewardPoint;
	@Column(name = "reward_exp", nullable = false)
	private int rewardExp;

	@ManyToOne
	@JoinColumn(name = "title_id")
	private Title rewardTitle;

	public Quest(String title, String description, QuestConditionType questConditionType,
				 int acquireCondition, int rewardPoint, int rewardExp, Title rewardTitle) {
		this.title = title;
		this.description = description;
		this.questConditionType = questConditionType;
		this.acquireCondition = acquireCondition;
		this.rewardPoint = rewardPoint;
		this.rewardExp = rewardExp;
		this.rewardTitle = rewardTitle;
	}

	public void update(QuestUpdateRequestDto dto, Title rewardTitle) {
		if (dto.getTitle() != null)
			this.title = dto.getTitle();
		if (dto.getDescription() != null)
			this.description = dto.getDescription();
		if (dto.getQuestConditionType() != null)
			this.questConditionType = dto.getQuestConditionType();
		if (dto.getAcquireCondition() != null)
			this.acquireCondition = dto.getAcquireCondition();
		if (dto.getRewardPoint() != null)
			this.rewardPoint = dto.getRewardPoint();
		if (dto.getRewardExp() != null)
			this.rewardExp = dto.getRewardExp();
		if (rewardTitle != null)
			this.rewardTitle = rewardTitle;
	}
}
