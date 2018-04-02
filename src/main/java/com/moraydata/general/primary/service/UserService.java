package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface UserService {
	
	User create(User instance);
	User update(User instance);
	long delete(Long... instanceIds);
	User get(Long instanceId);
	List<User> get(Long... instanceIds);
	Page<User> get(Pageable pageable);
	Page<User> get(Pageable pageable, Predicate predicate);
	List<User> get();
	BooleanExpression search(JSONObject conditions);
	
	User get(String username);
	boolean isUsernameUnique(String username, Long userId);
	boolean isNicknameUnique(String nickname, Long userId);

	boolean updatePassword(Long userId, String newPassword, String oldPassword);
	boolean updatePassword(String key, String code, String newPassword);
	boolean exists(String username, String phone);
	String sendMessageCode(String username, String phone, long currentClientMilliseconds);
}
