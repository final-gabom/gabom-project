package com.explorer.gabom.domain.quest.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuest is a Querydsl query type for Quest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuest extends EntityPathBase<Quest> {

    private static final long serialVersionUID = 1544895683L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuest quest = new QQuest("quest");

    public final com.explorer.gabom.global.entity.QBaseTimeEntity _super = new com.explorer.gabom.global.entity.QBaseTimeEntity(this);

    public final NumberPath<Integer> acquireCondition = createNumber("acquireCondition", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.explorer.gabom.domain.quest.type.QuestConditionType> questConditionType = createEnum("questConditionType", com.explorer.gabom.domain.quest.type.QuestConditionType.class);

    public final NumberPath<Integer> rewardExp = createNumber("rewardExp", Integer.class);

    public final NumberPath<Integer> rewardPoint = createNumber("rewardPoint", Integer.class);

    public final com.explorer.gabom.domain.title.entity.QTitle rewardTitle;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QQuest(String variable) {
        this(Quest.class, forVariable(variable), INITS);
    }

    public QQuest(Path<? extends Quest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuest(PathMetadata metadata, PathInits inits) {
        this(Quest.class, metadata, inits);
    }

    public QQuest(Class<? extends Quest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.rewardTitle = inits.isInitialized("rewardTitle") ? new com.explorer.gabom.domain.title.entity.QTitle(forProperty("rewardTitle")) : null;
    }

}

