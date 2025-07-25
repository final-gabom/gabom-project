package com.explorer.gabom.global.file.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAttachmentFile is a Querydsl query type for AttachmentFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttachmentFile extends EntityPathBase<AttachmentFile> {

    private static final long serialVersionUID = -2078738983L;

    public static final QAttachmentFile attachmentFile = new QAttachmentFile("attachmentFile");

    public final StringPath fileId = createString("fileId");

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final EnumPath<com.explorer.gabom.global.file.type.FileType> fileType = createEnum("fileType", com.explorer.gabom.global.file.type.FileType.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final StringPath mimeType = createString("mimeType");

    public final NumberPath<Integer> orderIdx = createNumber("orderIdx", Integer.class);

    public final NumberPath<Long> refId = createNumber("refId", Long.class);

    public QAttachmentFile(String variable) {
        super(AttachmentFile.class, forVariable(variable));
    }

    public QAttachmentFile(Path<? extends AttachmentFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAttachmentFile(PathMetadata metadata) {
        super(AttachmentFile.class, metadata);
    }

}

