package com.moraydata.general.primary.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QInvitationCode is a Querydsl query type for InvitationCode
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QInvitationCode extends EntityPathBase<InvitationCode> {

    private static final long serialVersionUID = 1125118166L;

    public static final QInvitationCode invitationCode = new QInvitationCode("invitationCode");

    public final QBaseAbstractEntity _super = new QBaseAbstractEntity(this);

    public final BooleanPath available = createBoolean("available");

    public final StringPath code = createString("code");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> expiredDate = createDateTime("expiredDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QInvitationCode(String variable) {
        super(InvitationCode.class, forVariable(variable));
    }

    public QInvitationCode(Path<? extends InvitationCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInvitationCode(PathMetadata metadata) {
        super(InvitationCode.class, metadata);
    }

}

