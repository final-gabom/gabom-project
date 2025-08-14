package com.explorer.gabom.domain.missionproof.dto.response;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;

import lombok.Getter;

@Getter
public class MissionProofSearchCondition {

	private final MissionProofType fieldType;  // PLACE 또는 EVENT
	private final Long typeId;          // 장소 또는 이벤트 ID
	private final Long userId;          // 특정 유저 ID

	public MissionProofSearchCondition(
		MissionProofType fieldType,
		Long typeId,
		Long userId
	) {
		this.fieldType = fieldType;
		this.typeId = typeId;
		this.userId = userId;
	}

	// 조회 조건 확인용

	public boolean hasTypeCondition() {
		return fieldType != null && typeId != null;
	}

	public boolean hasUserCondition() {
		return userId != null;
	}
}
