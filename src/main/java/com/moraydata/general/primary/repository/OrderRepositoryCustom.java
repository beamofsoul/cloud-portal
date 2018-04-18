package com.moraydata.general.primary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.moraydata.general.primary.entity.Order;
import com.moraydata.general.primary.entity.Order.Status;
import com.querydsl.core.types.Predicate;

public interface OrderRepositoryCustom<T,ID> {

	long updateStatusById(Status status, Long orderId);
	Order findOneFull(Long orderId);
	Page<Order> findAll(Predicate predicate, Pageable pageable);
}
