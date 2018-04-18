package com.moraydata.general.primary.repository;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import com.moraydata.general.management.repository.BaseMultielementRepository;
import com.moraydata.general.primary.entity.Order;

@Repository
public interface OrderRepository extends BaseMultielementRepository<Order, Long>, OrderRepositoryCustom<Order, Long> {

	@Procedure("getNextSequenceValue")
	Integer nextSequenceValue(String name);
}
