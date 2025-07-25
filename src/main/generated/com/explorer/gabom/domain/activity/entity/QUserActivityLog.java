package com.explorer.gabom.domain.activity.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserActivityLog is a Querydsl query type for UserActivityLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserActivityLog extends EntityPathBase<UserActivityLog> {

    private static final long serialVersionUID = -138046914L;

    public static final QUserActivityLog userActivityLog = new QUserActivityLog("userActivityLog");

    public final EnumPath<com.explorer.gabom.domain.activity.type.ActivityType> activityType = createEnum("activityType", com.explorer.gabom.domain.activity.type.ActivityType.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ipAddress = createString("ipAddress");

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserActivityLog(String variable) {
        super(UserActivityLog.class, forVariable(variable));
    }

    public QUserActivityLog(Path<? extends UserActivityLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserActivityLog(PathMetadata metadata) {
        super(UserActivityLog.class, metadata);
    }

}

