package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.JobAndTrigger;
import com.moraydata.general.primary.entity.query.QJobAndTrigger;
import com.moraydata.general.primary.repository.JobAndTriggerRepository;
import com.moraydata.general.primary.service.JobAndTriggerService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service("jobAndTriggerService")
@Transactional(readOnly = true)
public class JobAndTriggerServiceImpl implements JobAndTriggerService {

	@Autowired
	private JobAndTriggerRepository jobAndTriggerRepository;

	@Override
	public JobAndTrigger get(Long instanceId) {
		return jobAndTriggerRepository.findOne(instanceId);
	}
	
	@Override
	public List<JobAndTrigger> get(Long... instanceIds) {
		return jobAndTriggerRepository.findByIds(instanceIds);
	}

	@Override
	public List<JobAndTrigger> get() {
		return jobAndTriggerRepository.findAll();
	}

	@Override
	public Page<JobAndTrigger> get(Pageable pageable) {
		return jobAndTriggerRepository.findAll(pageable);
	}

	@Override
	public Page<JobAndTrigger> get(Pageable pageable, Predicate predicate) {
		return jobAndTriggerRepository.findAll(predicate, pageable);
	}
	
	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;
		
		QJobAndTrigger jobAndTrigger = QJobAndTrigger.jobAndTrigger;
		BooleanExpression exp = null;
		
		String jobName = conditions.getString(jobAndTrigger.jobName.getMetadata().getName());
		exp = addExpression(jobName, exp, jobAndTrigger.jobName.like(like(jobName)));

		String jobGroup = conditions.getString(jobAndTrigger.jobGroup.getMetadata().getName());
		exp = addExpression(jobGroup, exp, jobAndTrigger.jobGroup.like(like(jobGroup)));
		
		String jobClassName = conditions.getString(jobAndTrigger.jobClassName.getMetadata().getName());
		exp = addExpression(jobClassName, exp, jobAndTrigger.jobClassName.like(like(jobClassName)));
		
		String triggerName = conditions.getString(jobAndTrigger.triggerName.getMetadata().getName());
		exp = addExpression(triggerName, exp, jobAndTrigger.triggerName.like(like(triggerName)));
		
		String triggerGroup = conditions.getString(jobAndTrigger.triggerGroup.getMetadata().getName());
		exp = addExpression(triggerGroup, exp, jobAndTrigger.triggerGroup.like(like(triggerGroup)));
		
		String cronExpression = conditions.getString(jobAndTrigger.cronExpression.getMetadata().getName());
		exp = addExpression(cronExpression, exp, jobAndTrigger.cronExpression.like(like(cronExpression)));
		
		String timeZoneId = conditions.getString(jobAndTrigger.timeZoneId.getMetadata().getName());
		exp = addExpression(timeZoneId, exp, jobAndTrigger.timeZoneId.like(like(timeZoneId)));
		
		return exp;
	}
}
