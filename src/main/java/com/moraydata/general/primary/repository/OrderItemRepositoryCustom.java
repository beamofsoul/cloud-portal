package com.moraydata.general.primary.repository;

import java.util.List;

import com.moraydata.general.primary.entity.OrderItem;

public interface OrderItemRepositoryCustom<T,ID> {

	List<OrderItem> findAllByUserId(Long userId);
}
