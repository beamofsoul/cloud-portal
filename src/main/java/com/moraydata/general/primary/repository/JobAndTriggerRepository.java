package com.moraydata.general.primary.repository;

import org.springframework.stereotype.Repository;

import com.moraydata.general.management.repository.BaseMultielementRepository;
import com.moraydata.general.primary.entity.JobAndTrigger;

@Repository
public interface JobAndTriggerRepository extends BaseMultielementRepository<JobAndTrigger, Long> {

}
