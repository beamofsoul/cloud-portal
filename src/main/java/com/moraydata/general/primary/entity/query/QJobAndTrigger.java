package com.moraydata.general.primary.entity.query;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.moraydata.general.primary.entity.JobAndTrigger;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;


/**
 * QJobAndTrigger is a Querydsl query type for JobAndTrigger
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QJobAndTrigger extends EntityPathBase<JobAndTrigger> {

    private static final long serialVersionUID = 1393209806L;

    public static final QJobAndTrigger jobAndTrigger = new QJobAndTrigger("jobAndTrigger");

    public final StringPath cronExpression = createString("cronExpression");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jobClassName = createString("jobClassName");

    public final StringPath jobGroup = createString("jobGroup");

    public final StringPath jobName = createString("jobName");

    public final StringPath timeZoneId = createString("timeZoneId");

    public final StringPath triggerGroup = createString("triggerGroup");

    public final StringPath triggerName = createString("triggerName");

    public QJobAndTrigger(String variable) {
        super(JobAndTrigger.class, forVariable(variable));
    }

    public QJobAndTrigger(Path<? extends JobAndTrigger> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJobAndTrigger(PathMetadata metadata) {
        super(JobAndTrigger.class, metadata);
    }

}

