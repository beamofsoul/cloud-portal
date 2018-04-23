package com.moraydata.general.primary.repository;

import java.util.List;

import com.moraydata.general.primary.entity.OrderItem;
import com.moraydata.general.primary.entity.Order.Status;

public interface OrderItemRepositoryCustom<T,ID> {

	long updateStatusById(Status status, Long orderItemId);
	OrderItem findOneWithServiceNameById(Long orderItemId);
	List<OrderItem> findAllByUserId(Long userId);
}
