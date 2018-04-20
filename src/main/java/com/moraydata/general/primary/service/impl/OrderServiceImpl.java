package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toInteger;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLocalDateTime;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;
import static com.moraydata.general.management.util.BooleanExpressionUtils.leftLike;
import static com.moraydata.general.management.util.BooleanExpressionUtils.rightLike;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.primary.entity.Order;
import com.moraydata.general.primary.entity.Order.Status;
import com.moraydata.general.primary.entity.OrderItem;
import com.moraydata.general.primary.entity.query.QOrder;
import com.moraydata.general.primary.repository.OrderRepository;
import com.moraydata.general.primary.service.OrderItemService;
import com.moraydata.general.primary.service.OrderService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service("orderService")
public class OrderServiceImpl extends BaseAbstractService implements OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderItemService orderItemService;

//	@Override
//	public Order create(Order instance) {
//		return orderRepository.save(instance);
//	}

	@Override
	public Order update(Order instance) {
		Order originalOrder = orderRepository.findOneFull(instance.getId());
		BeanUtils.copyProperties(instance, originalOrder);
		return orderRepository.save(originalOrder);
	}

	@Override
	@Transactional
	public long delete(Long... instanceIds) throws Exception {
		return orderRepository.deleteByIds(instanceIds);
	}

	@Override
	public Order get(Long instanceId) {
		return orderRepository.findOneFull(instanceId);
	}
	

	@Override
	public List<Order> get(Long... instanceIds) {
		return orderRepository.findByIds(instanceIds);
	}

	@Override
	public List<Order> get() {
		return orderRepository.findAll();
	}

	@Override
	public Page<Order> get(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}

	@Override
	public Page<Order> get(Pageable pageable, Predicate predicate) {
		return orderRepository.findAll(predicate, pageable);
	}
	
	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;
		
		QOrder order = QOrder.order;
		BooleanExpression exp = null;
		
		String id = conditions.getString(order.id.getMetadata().getName());
		exp = addExpression(id, exp, order.id.eq(toLong(id)));
		
		String code = conditions.getString(order.code.getMetadata().getName());
		exp = addExpression(code, exp, order.code.like(like(code)));
		
		String agent = conditions.getString(order.agent.getMetadata().getName());
		exp = addExpression(agent, exp, order.agent.like(like(agent)));
		
		String operator = conditions.getString(order.operator.getMetadata().getName());
		exp = addExpression(operator, exp, order.operator.like(like(operator)));
		
		String description = conditions.getString(order.description.getMetadata().getName());
		exp = addExpression(description, exp, order.description.like(like(description)));
		
		String serviceIds = conditions.getString(order.serviceIds.getMetadata().getName());
		if (StringUtils.isNotBlank(serviceIds)) {
			exp = addExpression(serviceIds, exp, order.serviceIds.eq(serviceIds).or(order.serviceIds.like(leftLike("," + serviceIds)).or(order.serviceIds.like(rightLike(serviceIds + ",")).or(order.serviceIds.like(like(serviceIds))))));
		}

		String userId = conditions.getString(order.userId.getMetadata().getName());
		exp = addExpression(userId, exp, order.userId.eq(toLong(userId)));
		
		String status = conditions.getString(order.status.getMetadata().getName());
		exp = addExpression(status, exp, order.status.eq(toInteger(status)));
		
		String serviceBeginTime = conditions.getString(order.serviceBeginTime.getMetadata().getName());
		exp = addExpression(serviceBeginTime, exp, order.serviceBeginTime.after(toLocalDateTime(serviceBeginTime)));
		
		String serviceEndTime = conditions.getString(order.serviceEndTime.getMetadata().getName());
		exp = addExpression(serviceEndTime, exp, order.serviceEndTime.after(toLocalDateTime(serviceEndTime)));
		
		return exp;
	}
	
	/*******************************************************************************************************************/
	
	/**
	 * For Open API
	 * @param order
	 * @param originalOrder
	 * @return Order
	 * @throws Expcetion
	 */
	@Transactional(readOnly = false)
	@Override
	public Order update(Order order, Order originalOrder) throws Exception {
		BeanUtils.copyProperties(order, originalOrder);
		Order data =  orderRepository.save(originalOrder);
		dealWithOrderItems(data);
		return data;
	}

	/**
	 * For Open API
	 * @param data
	 * @return void
	 * @throws Exception
	 */
	private void dealWithOrderItems(Order data) throws Exception {
		// 创建或修改OrderItem
		Long userId = data.getUserId();
		LocalDateTime serviceBeginTime = data.getServiceBeginTime();
		LocalDateTime serviceEndTime = data.getServiceEndTime();
		Integer status = data.getStatus();
		String description = data.getDescription();
		String[] services = data.getServiceIds().split(",");
		Long[] serviceIds = Arrays.asList(services).stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toList()).toArray(new Long[] {});
		
		List<OrderItem> orderItems = new ArrayList<OrderItem>(); // 最终装载OrderItem的列表
		List<OrderItem> listOfOrderItem = orderItemService.get(userId, serviceIds); // 从数据库中查询到的匹配的OrderItem的列表
		
		if (listOfOrderItem == null || listOfOrderItem.isEmpty()) { // 如果该用户之前没有任何订单，也就没有是没有任何OrderItem记录
			// 全部添加
			for (Long serviceId : serviceIds) {
				orderItems.add(createOrderItem(userId, serviceBeginTime, serviceEndTime, status, description, serviceId));
			}
		} else if (listOfOrderItem.size() == services.length) { // 如果改用户之前已经存在订单，并且购买了当前订单中记录的所有服务，也就是存在的OrderItem与当前订单服务记录个数1比1
			// 修改全部
			listOfOrderItem.stream().forEach(e -> {
				OrderItem updated = updateOrderItem(serviceEndTime, status, description, e);
				orderItems.add(updated);
			});
		} else {
			// 部分修改
			Map<Long, OrderItem> mapOfOrderItem = listOfOrderItem.stream().collect(Collectors.toMap(OrderItem::getServiceId, orderItem -> orderItem));
			for (Long serviceId : serviceIds) {
				if (mapOfOrderItem.containsKey(serviceId)) {
					// 存在即修改
					orderItems.add(updateOrderItem(serviceEndTime, status, description, mapOfOrderItem.get(serviceId)));
				} else {
					// 不存在即创建
					orderItems.add(createOrderItem(userId, serviceBeginTime, serviceEndTime, status, description, serviceId));
				}
			}
		}
		
		if (!orderItems.isEmpty()) {
			data.setOrderItems(orderItems);
		}
	}

	/**
	 * For Open API
	 * @return integer
	 * @throws Exception
	 */
	@Override
	public Integer getNextOrderCode() throws Exception {
		return orderRepository.nextSequenceValue(Constants.ORDER.CODE_SEQUENCE_NAME);
	}

	/**
	 * For Open API
	 * @param order
	 * @return Order
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public Order create(Order instance) throws Exception {
		Order data = orderRepository.save(instance);
		dealWithOrderItems(data);
		return data;
	}

	private OrderItem updateOrderItem(LocalDateTime serviceEndTime, Integer status, String description, OrderItem orderItem) {
		orderItem.setServiceEndTime(serviceEndTime);
		orderItem.setStatus(status);
		orderItem.setDescription(description);
		OrderItem updated = orderItemService.update(orderItem);
		return updated;
	}

	private OrderItem createOrderItem(Long userId, LocalDateTime serviceBeginTime, LocalDateTime serviceEndTime, Integer status,
			String description, Long serviceId) {
		OrderItem orderItem = new OrderItem();
		orderItem.setUserId(userId);
		orderItem.setServiceId(serviceId);
		orderItem.setServiceBeginTime(serviceBeginTime);
		orderItem.setServiceEndTime(serviceEndTime);
		orderItem.setStatus(status);
		orderItem.setDescription(description);
		return orderItemService.create(orderItem);
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updateStatus(Long orderId, Status status) throws Exception {
		return orderRepository.updateStatusById(status, orderId) > 0;
	}
}
