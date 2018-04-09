package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.Permission;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface PermissionService {

	Permission create(Permission instance);
	Permission update(Permission instance);
	long delete(Long... instanceIds);
	
	Permission get(Long instanceId);
	List<Permission> get(Long... instanceIds);
	Page<Permission> get(Pageable pageable);
	Page<Permission> get(Pageable pageable, Predicate predicate);
	List<Permission> get();
	BooleanExpression search(JSONObject conditions);
	List<Permission> getAllAvailable();
	
	Permission get(String name);

	boolean isPermissionNameUnique(String name, Long instanceId);
	boolean isUsedPermissions(Long... instanceId);
	
	boolean hasPermission(String action);
	List<Permission> getRelational(Long parentPermissionId);
	
	/*******************************************************************************************************************/
	
	Permission update(Permission permission, Permission originalPermission) throws Exception;
}
