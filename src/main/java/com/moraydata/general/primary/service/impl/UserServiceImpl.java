package com.moraydata.general.primary.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;
import com.moraydata.general.primary.entity.query.QUser;
import com.moraydata.general.primary.repository.UserRepository;
import com.moraydata.general.primary.service.UserService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	@Override
	public User get(Long instanceId) {
		User one = userRepository.findOne(instanceId);
		if (one != null) {
			Hibernate.initialize(one.getRoles());
		}
		return one;
	}

	/**
	 * 通过输入的1级用户的userId，找到其主账号(包括主账号)所辖所有2或3级设置了接收推送消息的子账号
	 * PS: 1级用户未必是主账号
	 */
	@Override
	public List<UserBasicInformation> getLevelUserBasicInformation(Long level1UserId, User.Level... level) {
		QUser $ = new QUser("User");
		QueryResults<?> queryResult = userRepository.findSpecificData($.id.eq(level1UserId), $.parentId);
		Long level1UserParentId = ((Tuple) queryResult.getResults().get(0)).get($.parentId);
		Long masterUserId = (level1UserParentId == null || level1UserParentId == 0) ? level1UserId : level1UserParentId;
		
		int[] levels = null;
		if (level != null && level.length > 0) {
			levels = new int[level.length];
			for (int i = 0; i < level.length; i++) {
				levels[i] = level[i].getValue();
			}
		} else {
			return null;
		}
		
		return userRepository.findLevelUserBasicInformation(masterUserId, levels);
	}
}
