package com.moraydata.general.primary.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;

import com.moraydata.general.management.util.QuerydslUtils;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;
import com.moraydata.general.primary.entity.query.QUser;
import com.moraydata.general.primary.repository.UserRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

@NoRepositoryBean
public class UserRepositoryImpl implements UserRepositoryCustom<User, Long> {
	
	@Autowired
	private EntityManager entityManager;

	/**
	 * 获取有openId的1级用户的用户Id、username和openId
	 */
	@Override
	public List<UserBasicInformation> findAllIdAndUsernameWhoHasOpenId() {
		JPAQuery<User> query = QuerydslUtils.newQuery(entityManager);
		QUser $ = new QUser("user");
		return query.select(Projections.constructor(UserBasicInformation.class, 
					$.id,
					$.username,
					$.openId,
					$.notifiedWarningPublicSentiment,
					$.notifiedHotPublicSentiment,
					$.notifiedNegativePublicSentiment))
				.from($)
				.where($.openId.isNotNull().and($.openId.isNotEmpty()).and($.level.eq(1)).and($.notified.isTrue()))
				.orderBy($.id.asc())
				.fetch();
	}

	/**
	 * 根据主账号Id(parentId)获取其下(如果主账号自己符合条件也会被包括在内)所有用户级别为3的、允许接收推送消息的子账号
	 */
	@Override
	public List<UserBasicInformation> findLevel3UserBasicInformation(Long parentId) {
		JPAQuery<User> query = QuerydslUtils.newQuery(entityManager);
		QUser $ = new QUser("user");
		return query.select(Projections.constructor(UserBasicInformation.class,
					$.id,
					$.username,
					$.openId,
					$.notifiedWarningPublicSentiment,
					$.notifiedHotPublicSentiment,
					$.notifiedNegativePublicSentiment))
				.from($)
				.where(($.parentId.eq(parentId).or($.id.eq(parentId))).and($.level.eq(3)).and($.notified.isTrue()))
				.orderBy($.id.asc())
				.fetch();
	}
	
	/**
	 * 根据主账号Id(parentId)获取其下(如果主账号自己符合条件也会被包括在内)所有用户级别为2或3的、允许接收推送消息的子账号
	 */
	@Override
	public List<UserBasicInformation> findLevel2Or3UserBasicInformation(Long parentId) {
		JPAQuery<User> query = QuerydslUtils.newQuery(entityManager);
		QUser $ = new QUser("user");
		return query.select(Projections.constructor(UserBasicInformation.class,
					$.id,
					$.username,
					$.openId,
					$.notifiedWarningPublicSentiment,
					$.notifiedHotPublicSentiment,
					$.notifiedNegativePublicSentiment))
				.from($)
				.where(($.parentId.eq(parentId).or($.id.eq(parentId))).and(($.level.eq(3).or($.level.eq(2)))).and($.notified.isTrue()))
				.orderBy($.id.asc())
				.fetch();
	}
}
