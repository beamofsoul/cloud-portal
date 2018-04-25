package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.JobAndTrigger;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface JobAndTriggerService {
	
	JobAndTrigger get(Long instanceId);
	List<JobAndTrigger> get(Long... instanceIds);
	Page<JobAndTrigger> get(Pageable pageable);
	Page<JobAndTrigger> get(Pageable pageable, Predicate predicate);
	List<JobAndTrigger> get();
	BooleanExpression search(JSONObject conditions);
}
