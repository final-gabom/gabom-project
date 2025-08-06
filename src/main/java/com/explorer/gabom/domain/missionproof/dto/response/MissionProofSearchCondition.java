package com.explorer.gabom.domain.missionproof.dto.response;

import com.explorer.gabom.domain.missionproof.type.MissionProofType;

import lombok.Getter;

@Getter
public class MissionProofSearchCondition {

	private final MissionProofType fieldType;  // PLACE 또는 EVENT
	private final Long typeId;          // 장소 또는 이벤트 ID
	private final Long userId;          // 특정 유저 ID
	private final Long lastId;          // 커서 기반 조회를 위한 마지막 ID
	private final int size;             // 조회할 개수 (limit)

	public MissionProofSearchCondition(
		MissionProofType fieldType,
		Long typeId,
		Long userId,
		Long lastId,
		int size
	) {
		this.fieldType = fieldType;
		this.typeId = typeId;
		this.userId = userId;
		this.lastId = lastId;
		this.size = size;
	}

	// 조회 조건 확인용

	public boolean hasTypeCondition() {
		return fieldType != null && typeId != null;
	}

	public boolean hasUserCondition() {
		return userId != null;
	}

	public boolean isCursorPaging() {
		return lastId != null;
	}

}
