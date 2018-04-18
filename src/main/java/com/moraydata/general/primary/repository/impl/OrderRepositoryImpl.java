package com.moraydata.general.primary.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.moraydata.general.management.util.PageImpl;
import com.moraydata.general.management.util.QuerydslUtils;
import com.moraydata.general.primary.entity.Order;
import com.moraydata.general.primary.entity.Order.Status;
import com.moraydata.general.primary.entity.query.QOrder;
import com.moraydata.general.primary.entity.query.QUser;
import com.moraydata.general.primary.repository.OrderRepositoryCustom;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

public class OrderRepositoryImpl implements OrderRepositoryCustom<Order, Long> {

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public long updateStatusById(Status status, Long orderId) {
		QOrder order = QOrder.order;
		return QuerydslUtils.newUpdate(entityManager, order).set(order.status, status.getValue()).where(order.id.eq(orderId)).execute();
	}
	
	@Override
	public Order findOneFull(Long orderId) {
		QOrder order = QOrder.order;
		QUser user = new QUser("User");
		JPAQuery<Order> query = QuerydslUtils.newQuery(entityManager);
		Order fetchOne = query.select(Projections.constructor(Order.class,
									order.id,
									order.userId,
									user.username,
									order.code,
									order.amount,
									order.agent,
									order.amountForAgent,
									order.operator,
									order.serviceIds,
									order.description,
									order.serviceBeginTime,
									order.serviceEndTime,
									order.status))
								.from(order)
								.leftJoin(user)
								.on(order.userId.eq(user.id))
								.where(order.id.eq(orderId))
								.fetchOne();
		return fetchOne;
	}

	@Override
	public Page<Order> findAll(Predicate predicate, Pageable pageable) {
		// 首先查询分页数据，获取需要的OrderId列表
//		QOrder order = new QOrder("Order");
		QOrder order = QOrder.order;
		JPAQuery<Long> query0 = QuerydslUtils.initQuery(entityManager, order, pageable, predicate);
		query0.select(order.id);
		QueryResults<Long> fetchResults = query0.fetchResults();
		// 通过查询到的OrderId列表直接获取每条记录的具体数据
		JPAQuery<Order> query = QuerydslUtils.newQuery(entityManager);
		QUser user = new QUser("User");
		List<Order> list = query.select(Projections.constructor(Order.class,
									order.id,
									order.userId,
									user.username,
									order.code,
									order.amount,
									order.agent,
									order.amountForAgent,
									order.operator,
									order.serviceIds,
									order.description,
									order.serviceBeginTime,
									order.serviceEndTime,
									order.status))
								.from(order)
								.leftJoin(user)
								.on(order.userId.eq(user.id))
								.where(order.id.in(fetchResults.getResults()))
								.orderBy(order.id.asc())
								.fetch();
		return new PageImpl<Order>(list, pageable, fetchResults.getTotal());
	}
}
