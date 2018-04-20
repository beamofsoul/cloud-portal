package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface OrderService {
	
//	Order create(Order instance);
	Order update(Order instance);
	long delete(Long... instanceIds) throws Exception;
	Order get(Long instanceId);
	List<Order> get(Long... instanceIds);
	Page<Order> get(Pageable pageable);
	Page<Order> get(Pageable pageable, Predicate predicate);
	List<Order> get();
	BooleanExpression search(JSONObject conditions);
	
	/*******************************************************************************************************************/
	
	Order update(Order order, Order originalOrder) throws Exception;
	Integer getNextOrderCode() throws Exception;
	Order create(Order instance) throws Exception;
	boolean updateStatus(Long orderId, Order.Status status) throws Exception;
}
