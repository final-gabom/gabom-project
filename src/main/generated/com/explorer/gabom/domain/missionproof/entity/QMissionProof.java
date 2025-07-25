package com.explorer.gabom.domain.missionproof.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMissionProof is a Querydsl query type for MissionProof
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMissionProof extends EntityPathBase<MissionProof> {

    private static final long serialVersionUID = -1587592211L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMissionProof missionProof = new QMissionProof("missionProof");

    public final com.explorer.gabom.global.entity.QBaseTimeEntity _super = new com.explorer.gabom.global.entity.QBaseTimeEntity(this);

    public final com.explorer.gabom.global.file.entity.QAttachmentFile attachmentImg;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.explorer.gabom.domain.place.entity.QPlace place;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMissionProof(String variable) {
        this(MissionProof.class, forVariable(variable), INITS);
    }

    public QMissionProof(Path<? extends MissionProof> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMissionProof(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMissionProof(PathMetadata metadata, PathInits inits) {
        this(MissionProof.class, metadata, inits);
    }

    public QMissionProof(Class<? extends MissionProof> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.attachmentImg = inits.isInitialized("attachmentImg") ? new com.explorer.gabom.global.file.entity.QAttachmentFile(forProperty("attachmentImg")) : null;
        this.place = inits.isInitialized("place") ? new com.explorer.gabom.domain.place.entity.QPlace(forProperty("place"), inits.get("place")) : null;
    }

}

