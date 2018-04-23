package com.moraydata.general.primary.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;

import com.moraydata.general.management.util.QuerydslUtils;
import com.moraydata.general.primary.entity.Order.Status;
import com.moraydata.general.primary.entity.OrderItem;
import com.moraydata.general.primary.entity.query.QOrderItem;
import com.moraydata.general.primary.entity.query.QService;
import com.moraydata.general.primary.repository.OrderItemRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

@NoRepositoryBean
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom<OrderItem, Long> {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public long updateStatusById(Status status, Long orderItemId) {
		QOrderItem orderItem = new QOrderItem("OrderItem");
		return QuerydslUtils.newUpdate(entityManager, orderItem).set(orderItem.status, status.getValue()).where(orderItem.id.eq(orderItemId)).execute();
	}
	
	@Override
	public OrderItem findOneWithServiceNameById(Long orderItemId) {
		
		JPAQuery<OrderItem> query = QuerydslUtils.newQuery(entityManager);
		QOrderItem $1 = new QOrderItem("OrderItem");
		QService $2 = new QService("Service");
		
		return query.select(Projections.constructor(OrderItem.class, 
										$1.id,
										$1.userId,
										$1.serviceId,
										$2.name,
										$1.description,
										$1.serviceBeginTime,
										$1.serviceEndTime,
										$1.status))
									.from($1)
									.rightJoin($2)
									.on($1.serviceId.eq($2.id))
									.where($1.id.eq(orderItemId))
									.fetchOne();
	}

	@Override
	public List<OrderItem> findAllByUserId(Long userId) {
		
		JPAQuery<OrderItem> query = QuerydslUtils.newQuery(entityManager);
		QOrderItem $1 = new QOrderItem("OrderItem");
		QService $2 = new QService("Service");
		
		List<OrderItem> list = query.select(Projections.constructor(OrderItem.class, 
										$1.id,
										$1.userId,
										$1.serviceId,
										$2.name,
										$1.description,
										$1.serviceBeginTime,
										$1.serviceEndTime,
										$1.status))
									.from($1)
									.rightJoin($2)
									.on($1.serviceId.eq($2.id))
									.where($2.available.eq(true).and($1.userId.eq(userId).or($1.userId.isNull())))
									.orderBy($2.id.asc())
									.fetch();
		return list;
	}
}
