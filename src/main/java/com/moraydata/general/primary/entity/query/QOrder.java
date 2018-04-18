package com.moraydata.general.primary.entity.query;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;

import com.moraydata.general.primary.entity.Order;
import com.querydsl.core.types.Path;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 1095030174L;

    public static final QOrder order = new QOrder("order1");

    public final QBaseAbstractEntity _super = new QBaseAbstractEntity(this);

    public final StringPath agent = createString("agent");

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> amountForAgent = createNumber("amountForAgent", java.math.BigDecimal.class);

    public final StringPath code = createString("code");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath operator = createString("operator");

    public final DateTimePath<java.time.LocalDateTime> serviceBeginTime = createDateTime("serviceBeginTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> serviceEndTime = createDateTime("serviceEndTime", java.time.LocalDateTime.class);

    public final StringPath serviceIds = createString("serviceIds");

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QOrder(String variable) {
        super(Order.class, forVariable(variable));
    }

    public QOrder(Path<? extends Order> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrder(PathMetadata metadata) {
        super(Order.class, metadata);
    }

}

