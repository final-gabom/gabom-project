package com.explorer.gabom.domain.place.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlaceFile is a Querydsl query type for PlaceFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlaceFile extends EntityPathBase<PlaceFile> {

    private static final long serialVersionUID = -2123652865L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlaceFile placeFile = new QPlaceFile("placeFile");

    public final com.explorer.gabom.global.file.entity.QAttachmentFile file;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> orderIdx = createNumber("orderIdx", Integer.class);

    public final QPlace place;

    public QPlaceFile(String variable) {
        this(PlaceFile.class, forVariable(variable), INITS);
    }

    public QPlaceFile(Path<? extends PlaceFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlaceFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlaceFile(PathMetadata metadata, PathInits inits) {
        this(PlaceFile.class, metadata, inits);
    }

    public QPlaceFile(Class<? extends PlaceFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.file = inits.isInitialized("file") ? new com.explorer.gabom.global.file.entity.QAttachmentFile(forProperty("file")) : null;
        this.place = inits.isInitialized("place") ? new QPlace(forProperty("place"), inits.get("place")) : null;
    }

}

