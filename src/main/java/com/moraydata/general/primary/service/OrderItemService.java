package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.OrderItem;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface OrderItemService {
	
	OrderItem create(OrderItem instance);
	OrderItem update(OrderItem instance);
	long delete(Long... instanceIds);
	OrderItem get(Long instanceId);
	List<OrderItem> get(Long... instanceIds);
	Page<OrderItem> get(Pageable pageable);
	Page<OrderItem> get(Pageable pageable, Predicate predicate);
	List<OrderItem> get();
	BooleanExpression search(JSONObject conditions);
	
	/*******************************************************************************************************************/
	
	OrderItem update(OrderItem orderItem, OrderItem originalOrderItem) throws Exception;
	List<OrderItem> get(Long userId, Long... serviceIds) throws Exception;
	List<OrderItem> getByUserId(Long userId) throws Exception;
}
