package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toBoolean;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.Service;
import com.moraydata.general.primary.entity.query.QOrderItem;
import com.moraydata.general.primary.entity.query.QService;
import com.moraydata.general.primary.repository.OrderItemRepository;
import com.moraydata.general.primary.repository.ServiceRepository;
import com.moraydata.general.primary.service.ServiceService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@org.springframework.stereotype.Service("serviceService")
public class ServiceServiceImpl extends BaseAbstractService implements ServiceService {

	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;

	@Override
	public Service create(Service instance) {
		return serviceRepository.save(instance);
	}

	@Override
	public Service update(Service instance) {
		Service originalService = serviceRepository.findOne(instance.getId());
		BeanUtils.copyProperties(instance, originalService);
		return serviceRepository.save(originalService);
	}

	@Override
	@Transactional
	public long delete(Long... instanceIds) {
		return serviceRepository.deleteByIds(instanceIds);
	}

	@Override
	public Service get(Long instanceId) {
		return serviceRepository.findOne(instanceId);
	}
	

	@Override
	public List<Service> get(Long... instanceIds) {
		return serviceRepository.findByIds(instanceIds);
	}

	@Override
	public List<Service> get() {
		return serviceRepository.findAll();
	}

	@Override
	public Page<Service> get(Pageable pageable) {
		return serviceRepository.findAll(pageable);
	}

	@Override
	public Page<Service> get(Pageable pageable, Predicate predicate) {
		return serviceRepository.findAll(predicate, pageable);
	}
	
	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;
		
		QService service = QService.service;
		BooleanExpression exp = null;
		
		String id = conditions.getString(service.id.getMetadata().getName());
		exp = addExpression(id, exp, service.id.eq(toLong(id)));
		
		String name = conditions.getString(service.name.getMetadata().getName());
		exp = addExpression(name, exp, service.name.like(like(name)));
		
		String available = conditions.getString(service.available.getMetadata().getName());
		exp = addExpression(available, exp, service.available.eq(toBoolean(available)));
		
		return exp;
	}
	
	/*******************************************************************************************************************/
	
	/**
	 * For Open API
	 * @param service
	 * @param originalService
	 * @return Service
	 * @throws Expcetion
	 */
	@Override
	public Service update(Service service, Service originalService) throws Exception {
		BeanUtils.copyProperties(service, originalService);
		return serviceRepository.save(originalService);
	}

	/**
	 * For Open API
	 * @param serviceIds
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean isUsedService(Long... serviceIds) throws Exception {
		// 判断订单记录中是否有当前服务
		return orderItemRepository.exists(new QOrderItem("OrderItem").serviceId.in(serviceIds)); 
	}
}
