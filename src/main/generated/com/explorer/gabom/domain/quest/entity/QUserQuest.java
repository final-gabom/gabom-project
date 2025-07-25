package com.explorer.gabom.domain.quest.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserQuest is a Querydsl query type for UserQuest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserQuest extends EntityPathBase<UserQuest> {

    private static final long serialVersionUID = 532098872L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserQuest userQuest = new QUserQuest("userQuest");

    public final DateTimePath<java.time.LocalDateTime> completedAt = createDateTime("completedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isRewardClaimed = createBoolean("isRewardClaimed");

    public final StringPath progressData = createString("progressData");

    public final EnumPath<com.explorer.gabom.domain.quest.type.ProgressStatus> progressStatus = createEnum("progressStatus", com.explorer.gabom.domain.quest.type.ProgressStatus.class);

    public final QQuest quest;

    public final NumberPath<Integer> rewardReceived = createNumber("rewardReceived", Integer.class);

    public final com.explorer.gabom.domain.user.entity.QUser user;

    public QUserQuest(String variable) {
        this(UserQuest.class, forVariable(variable), INITS);
    }

    public QUserQuest(Path<? extends UserQuest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserQuest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserQuest(PathMetadata metadata, PathInits inits) {
        this(UserQuest.class, metadata, inits);
    }

    public QUserQuest(Class<? extends UserQuest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.quest = inits.isInitialized("quest") ? new QQuest(forProperty("quest"), inits.get("quest")) : null;
        this.user = inits.isInitialized("user") ? new com.explorer.gabom.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

