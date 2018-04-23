package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toInteger;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLocalDateTime;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.OrderItem;
import com.moraydata.general.primary.entity.Order.Status;
import com.moraydata.general.primary.entity.query.QOrderItem;
import com.moraydata.general.primary.repository.OrderItemRepository;
import com.moraydata.general.primary.service.OrderItemService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service("orderItemService")
public class OrderItemServiceImpl extends BaseAbstractService implements OrderItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Override
	public OrderItem create(OrderItem instance) {
		return orderItemRepository.save(instance);
	}

	@Override
	public OrderItem update(OrderItem instance) {
		OrderItem originalOrderItem = orderItemRepository.findOne(instance.getId());
		BeanUtils.copyProperties(instance, originalOrderItem);
		return orderItemRepository.save(originalOrderItem);
	}

	@Override
	@Transactional
	public long delete(Long... instanceIds) {
		return orderItemRepository.deleteByIds(instanceIds);
	}

	@Override
	public OrderItem get(Long instanceId) {
		return orderItemRepository.findOneWithServiceNameById(instanceId);
	}
	

	@Override
	public List<OrderItem> get(Long... instanceIds) {
		return orderItemRepository.findByIds(instanceIds);
	}

	@Override
	public List<OrderItem> get() {
		return orderItemRepository.findAll();
	}

	@Override
	public Page<OrderItem> get(Pageable pageable) {
		return orderItemRepository.findAll(pageable);
	}

	@Override
	public Page<OrderItem> get(Pageable pageable, Predicate predicate) {
		return orderItemRepository.findAll(predicate, pageable);
	}
	
	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;
		
		QOrderItem orderItem = QOrderItem.orderItem;
		BooleanExpression exp = null;
		
		String id = conditions.getString(orderItem.id.getMetadata().getName());
		exp = addExpression(id, exp, orderItem.id.eq(toLong(id)));
		
		String userId = conditions.getString(orderItem.userId.getMetadata().getName());
		exp = addExpression(userId, exp, orderItem.userId.eq(toLong(userId)));
		
		String serviceId = conditions.getString(orderItem.serviceId.getMetadata().getName());
		exp = addExpression(serviceId, exp, orderItem.serviceId.eq(toLong(serviceId)));
		
		String description = conditions.getString(orderItem.description.getMetadata().getName());
		exp = addExpression(description, exp, orderItem.description.like(like(description)));
		
		String status = conditions.getString(orderItem.status.getMetadata().getName());
		exp = addExpression(status, exp, orderItem.status.eq(toInteger(status)));
		
		String serviceBeginTime = conditions.getString(orderItem.serviceBeginTime.getMetadata().getName());
		exp = addExpression(serviceBeginTime, exp, orderItem.serviceBeginTime.after(toLocalDateTime(serviceBeginTime)));
		
		String serviceEndTime = conditions.getString(orderItem.serviceEndTime.getMetadata().getName());
		exp = addExpression(serviceEndTime, exp, orderItem.serviceEndTime.after(toLocalDateTime(serviceEndTime)));
		
		return exp;
	}
	
	/*******************************************************************************************************************/
	
	/**
	 * For Open API
	 * @param orderItem
	 * @param originalOrderItem
	 * @return OrderItem
	 * @throws Expcetion
	 */
	@Override
	public OrderItem update(OrderItem orderItem, OrderItem originalOrderItem) throws Exception {
		BeanUtils.copyProperties(orderItem, originalOrderItem);
		return orderItemRepository.save(originalOrderItem);
	}

	/**
	 * For Open API
	 * @param userIds
	 * @param serviceIds
	 * @return List<OrderItem>
	 * @throws Expcetion
	 */
	@Override
	public List<OrderItem> get(Long userId, Long... serviceIds) throws Exception {
		QOrderItem $ = new QOrderItem("OrderItem");
		return orderItemRepository.findByPredicate($.userId.eq(userId).and($.serviceId.in(serviceIds)));
	}
	
	/**
	 * For Open API
	 * @param userIds
	 * @return List<OrderItem>
	 * @throws Expcetion
	 */
	@Override
	public List<OrderItem> getByUserId(Long userId) throws Exception {
		return orderItemRepository.findAllByUserId(userId);
	}
	
	/**
	 * For Open API
	 * @param orderItemId
	 * @param status
	 * @return boolean
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean updateStatus(Long orderItemId, Status status) throws Exception {
		return orderItemRepository.updateStatusById(status, orderItemId) > 0;
	}
}
