package com.moraydata.general.management.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.moraydata.general.management.system.RolePermissionsMapping;

/**
 * @ClassName SecurityPermissionEvaluator
 * @Description {@link org.springframework.security.access.PermissionEvaluator} implementation that customizes the way of evaluating permission.
 * @author MingshuJian
 * @Date 2017年1月19日 下午4:25:34
 * @version 1.0.0
 */
@Component
public class SecurityPermissionEvaluator implements PermissionEvaluator {

	/*
	 * (非 Javadoc)  
	 * <p>Title: hasPermission</p>  
	 * <p>Description: If current user has the permission that required.</p>  
	 * @param authentication
	 * @param targetDomainObject
	 * @param permission
	 * @return  
	 * @see org.springframework.security.access.PermissionEvaluator#hasPermission(org.springframework.security.core.Authentication, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return RolePermissionsMapping.contains(authentication.getAuthorities(), permission);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		return false;
	}
}
