package com.explorer.gabom.domain.place.mapper;

import com.explorer.gabom.domain.file.dto.ThumbnailDto;
import com.explorer.gabom.domain.file.entity.QAttachmentFile;
import com.explorer.gabom.domain.place.dto.response.PlaceSummary;
import com.explorer.gabom.domain.place.entity.QPlace;
import com.explorer.gabom.domain.title.entity.QTitle;
import com.explorer.gabom.domain.user.dto.UserSummaryDto;
import com.explorer.gabom.domain.user.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;

/**
 * QueryDsl Mapping 전용 클래스
 */
public class PlaceSummaryMapper {

	public static PlaceSummary fromTuple(Tuple t) {
		QPlace place = QPlace.place;
		QUser user = QUser.user;
		QTitle title = QTitle.title;
		QAttachmentFile file = QAttachmentFile.attachmentFile;
		NumberPath<Double> dist = Expressions.numberPath(Double.class, "distance");

		ThumbnailDto thumbnail = null;
		if (t.get(file.fileId) != null) {
			thumbnail = ThumbnailDto.builder()
									.fileId(t.get(file.fileId))
									.filePath(t.get(file.filePath))
									.build();
		}

		return PlaceSummary.builder()
						   .placeId(t.get(place.id))
						   .title(t.get(place.title))
						   .address(t.get(place.address))
						   .latitude(t.get(place.lat))
						   .longitude(t.get(place.lng))
						   .viewCount(t.get(place.viewCount))
						   .missionProofCount(0)
						   .avgRating(0.0)
						   .writer(UserSummaryDto.builder()
												 .id(t.get(user.id))
												 .nickname(t.get(user.nickname))
												 .level(t.get(user.level))
												 .title(t.get(title.name))
												 .build())
						   .thumbnail(thumbnail)
						   .distance(t.get(dist))
						   .build();
	}
}
