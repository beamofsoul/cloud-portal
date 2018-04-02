package com.moraydata.general.secondary.repository;

import org.springframework.stereotype.Repository;

import com.moraydata.general.management.repository.BaseMultielementRepository;
import com.moraydata.general.secondary.entity.Test;

@Repository
public interface TestRepository extends BaseMultielementRepository<Test, Long> {

}
