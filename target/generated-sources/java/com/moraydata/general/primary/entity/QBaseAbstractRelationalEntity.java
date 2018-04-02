package com.moraydata.general.primary.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseAbstractRelationalEntity is a Querydsl query type for BaseAbstractRelationalEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBaseAbstractRelationalEntity extends EntityPathBase<BaseAbstractRelationalEntity> {

    private static final long serialVersionUID = 593911245L;

    public static final QBaseAbstractRelationalEntity baseAbstractRelationalEntity = new QBaseAbstractRelationalEntity("baseAbstractRelationalEntity");

    public final QBaseAbstractEntity _super = new QBaseAbstractEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QBaseAbstractRelationalEntity(String variable) {
        super(BaseAbstractRelationalEntity.class, forVariable(variable));
    }

    public QBaseAbstractRelationalEntity(Path<? extends BaseAbstractRelationalEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseAbstractRelationalEntity(PathMetadata metadata) {
        super(BaseAbstractRelationalEntity.class, metadata);
    }

}

