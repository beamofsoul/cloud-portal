package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toBoolean;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.cache.CacheEvictBasedCollection;
import com.moraydata.general.management.cache.CacheableAvailableCollection;
import com.moraydata.general.management.cache.CacheableBasedPageableCollection;
import com.moraydata.general.management.cache.CacheableCommonCollection;
import com.moraydata.general.primary.entity.Service;
import com.moraydata.general.primary.entity.query.QOrderItem;
import com.moraydata.general.primary.entity.query.QService;
import com.moraydata.general.primary.repository.OrderItemRepository;
import com.moraydata.general.primary.repository.ServiceRepository;
import com.moraydata.general.primary.service.ServiceService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.stereotype.Service("serviceService")
@CacheConfig(cacheNames = ServiceServiceImpl.CACHE_NAME)
public class ServiceServiceImpl implements ServiceService {

	public static final String CACHE_NAME = "serviceCache";
	
	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;

	@Override
	@CachePut(key="#result.id", condition="#result ne null")
	public Service create(Service instance) {
		return serviceRepository.save(instance);
	}

	@Override
	@CachePut(key="result.id", condition="#result ne null")
	public Service update(Service instance) {
		Service originalService = serviceRepository.findOne(instance.getId());
		BeanUtils.copyProperties(instance, originalService);
		return serviceRepository.save(originalService);
	}

	@Override
	@Transactional
	@CacheEvictBasedCollection(key="#p0")
	public long delete(Long... instanceIds) {
		long count = 0L;
		try {
			if (instanceIds == null) {
				throw new IllegalArgumentException();
			}
			// If service has been used or not
			if (!isUsedServices(instanceIds)) {
				return serviceRepository.deleteByIds(instanceIds);
			}
		} catch (Exception e) {
			log.error(String.format("Illegal input parameter[%s] to delete [Service] objects", Arrays.toString(instanceIds)), e);
		}
		return count;
	}
	
	/**
	 * Determine whether any of the given isntance ids is used in the database table of T_ORDER_ITEM.
	 * @param  instanceIds - instance ids used to be checked.
	 * @return boolean - if any of them used return true, otherwise return false.
	 */
	@Override
	public boolean isUsedServices(@NonNull Long... instanceIds) {
		return orderItemRepository.exists(QOrderItem.orderItem.serviceId.in(instanceIds));
	}
	
	@CacheableAvailableCollection
	@Override
	public List<Service> getAllAvailable() {
		return null;
	}

	@Override
	@Cacheable(key="#result.id", condition="#result ne null")
	public Service get(Long instanceId) {
		return serviceRepository.findOne(instanceId);
	}
	
	@CacheableCommonCollection
	@Override
	public List<Service> get(Long... instanceIds) {
		return null;
	}

	@CacheableCommonCollection
	@Override
	public List<Service> get() {
		return serviceRepository.findAll();
	}

	@CacheableBasedPageableCollection
	@Transactional(readOnly = true)
	@Override
	public Page<Service> get(Pageable pageable) {
		return null;
	}

	@CacheableBasedPageableCollection
	@Transactional(readOnly=true)
	@Override
	public Page<Service> get(Pageable pageable, Predicate predicate) {
		return null;
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
	@CachePut(key="result.id", condition="#result ne null")
	@Override
	public Service update(Service service, Service originalService) throws Exception {
		BeanUtils.copyProperties(service, originalService);
		return serviceRepository.save(originalService);
	}
}
