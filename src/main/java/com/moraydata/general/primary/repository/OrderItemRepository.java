package com.moraydata.general.primary.repository;

import org.springframework.stereotype.Repository;

import com.moraydata.general.management.repository.BaseMultielementRepository;
import com.moraydata.general.primary.entity.OrderItem;

@Repository
public interface OrderItemRepository extends BaseMultielementRepository<OrderItem, Long> {

}
