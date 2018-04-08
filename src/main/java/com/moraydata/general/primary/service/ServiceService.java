package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.Service;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface ServiceService {
	
	Service create(Service instance);
	Service update(Service instance);
	long delete(Long... instanceIds);
	Service get(Long instanceId);
	List<Service> get(Long... instanceIds);
	Page<Service> get(Pageable pageable);
	Page<Service> get(Pageable pageable, Predicate predicate);
	List<Service> get();
	BooleanExpression search(JSONObject conditions);
	
	/*******************************************************************************************************************/
	
	Service update(Service service, Service originalService) throws Exception;
}
