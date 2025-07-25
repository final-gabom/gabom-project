package com.explorer.gabom.domain.activity.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdminActivityLog is a Querydsl query type for AdminActivityLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminActivityLog extends EntityPathBase<AdminActivityLog> {

    private static final long serialVersionUID = -634144750L;

    public static final QAdminActivityLog adminActivityLog = new QAdminActivityLog("adminActivityLog");

    public final EnumPath<com.explorer.gabom.domain.activity.type.ActivityType> activityType = createEnum("activityType", com.explorer.gabom.domain.activity.type.ActivityType.class);

    public final NumberPath<Long> adminId = createNumber("adminId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ipAddress = createString("ipAddress");

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public QAdminActivityLog(String variable) {
        super(AdminActivityLog.class, forVariable(variable));
    }

    public QAdminActivityLog(Path<? extends AdminActivityLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdminActivityLog(PathMetadata metadata) {
        super(AdminActivityLog.class, metadata);
    }

}

