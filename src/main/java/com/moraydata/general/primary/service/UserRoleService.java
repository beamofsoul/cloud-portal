package com.moraydata.general.primary.service;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.UserRole;
import com.moraydata.general.primary.entity.UserRoleCombineRole;

public interface UserRoleService {

	Page<UserRoleCombineRole> get(Pageable pageable);
	Page<UserRoleCombineRole> get(Pageable pageable, JSONObject conditions);
	UserRoleCombineRole get(Long userId);
	UserRoleCombineRole update(UserRoleCombineRole userRoleCombineRole);
	
	UserRole create(UserRole instance);
	Collection<UserRole> create(Collection<UserRole> userRoles);
	void delete(Long instanceId);
	Long delete(Long... instanceIds);
	Long delete(Long userId, Long[] roleIds);
}