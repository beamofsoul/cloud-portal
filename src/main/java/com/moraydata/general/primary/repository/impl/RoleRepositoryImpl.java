package com.moraydata.general.primary.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;

import com.moraydata.general.management.util.QuerydslUtils;
import com.moraydata.general.primary.entity.Role;
import com.moraydata.general.primary.entity.query.QRole;
import com.moraydata.general.primary.repository.RoleRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

@NoRepositoryBean
public class RoleRepositoryImpl<RolePermission> implements RoleRepositoryCustom<Role, Long> {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public List<Role> findAllAvailable() {
		JPAQuery<Role> query = QuerydslUtils.newQuery(entityManager);
		QRole $ = QRole.role;
		List<Role> roleList = 
			query.select(Projections.constructor(Role.class, 
					$.id,
					$.name,
					$.priority))
				.from($)
				.where($.available.eq(true))
				.orderBy($.priority.asc(),$.id.asc())
				.fetch();
		return roleList;
	}
}
