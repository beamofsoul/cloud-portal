package com.moraydata.general.primary.entity.query;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.moraydata.general.primary.entity.Login;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;


/**
 * QLogin is a Querydsl query type for Login
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QLogin extends EntityPathBase<Login> {

    private static final long serialVersionUID = -1515165546L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLogin login = new QLogin("login");

    public final QBaseAbstractEntity _super = new QBaseAbstractEntity(this);

    public final StringPath brand = createString("brand");

    public final StringPath browser = createString("browser");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ipAddress = createString("ipAddress");

    public final StringPath model = createString("model");

    public final StringPath operatingSystem = createString("operatingSystem");

    public final StringPath screenSize = createString("screenSize");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final QUser user;

    public QLogin(String variable) {
        this(Login.class, forVariable(variable), INITS);
    }

    public QLogin(Path<? extends Login> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLogin(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLogin(PathMetadata metadata, PathInits inits) {
        this(Login.class, metadata, inits);
    }

    public QLogin(Class<? extends Login> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

