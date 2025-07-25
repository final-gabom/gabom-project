package com.explorer.gabom.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -43061837L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.explorer.gabom.global.entity.QBaseTimeEntity _super = new com.explorer.gabom.global.entity.QBaseTimeEntity(this);

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    public final NumberPath<Integer> exp = createNumber("exp", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> lat = createNumber("lat", Double.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final NumberPath<Double> lng = createNumber("lng", Double.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final com.explorer.gabom.global.file.entity.QAttachmentFile profileImg;

    public final EnumPath<com.explorer.gabom.domain.user.type.UserStatus> status = createEnum("status", com.explorer.gabom.domain.user.type.UserStatus.class);

    public final com.explorer.gabom.domain.title.entity.QTitle title;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<com.explorer.gabom.domain.user.type.UserRole> userRole = createEnum("userRole", com.explorer.gabom.domain.user.type.UserRole.class);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profileImg = inits.isInitialized("profileImg") ? new com.explorer.gabom.global.file.entity.QAttachmentFile(forProperty("profileImg")) : null;
        this.title = inits.isInitialized("title") ? new com.explorer.gabom.domain.title.entity.QTitle(forProperty("title")) : null;
    }

}

