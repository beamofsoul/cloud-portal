package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toBoolean;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.cache.CacheEvictBasedCollection;
import com.moraydata.general.management.cache.CacheableAvailableCollection;
import com.moraydata.general.management.cache.CacheableBasedPageableCollection;
import com.moraydata.general.management.cache.CacheableCommonCollection;
import com.moraydata.general.management.security.SecurityPermissionEvaluator;
import com.moraydata.general.management.system.RolePermissionsMapping;
import com.moraydata.general.management.util.SpringUtils;
import com.moraydata.general.primary.entity.Permission;
import com.moraydata.general.primary.entity.query.QPermission;
import com.moraydata.general.primary.entity.query.QRolePermission;
import com.moraydata.general.primary.repository.PermissionRepository;
import com.moraydata.general.primary.repository.RolePermissionRepository;
import com.moraydata.general.primary.service.PermissionService;
import com.moraydata.general.primary.service.RolePermissionService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("permissionService")
@CacheConfig(cacheNames = PermissionServiceImpl.CACHE_NAME)
public class PermissionServiceImpl extends BaseAbstractService implements PermissionService {
	
	public static final String CACHE_NAME = "permissionCache";

	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private RolePermissionService rolePermissionService;
	
	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	
	@Override
	@CachePut(key="#result.id", condition="#result ne null")
	public Permission create(Permission instance) {
		try {
			if (!Permission.ResourceType.exists(instance.getResourceType())) {
				throw new IllegalArgumentException();
			}
			return permissionRepository.save(instance);
		} catch (Exception e) {
			log.error(String.format("Illegal input parameter[%s] to create [Permission] object", instance), e);
			return null;
		}
	}
	
	@Transactional
	@CachePut(key="#result.id", condition="#result ne null")
	@Override
	public Permission update(Permission instance) {
		try {
			if (!Permission.ResourceType.exists(instance.getResourceType())) {
				throw new IllegalArgumentException();
			}
			Permission originalInstance = get(instance.getId());
			BeanUtils.copyProperties(instance, originalInstance);
			return permissionRepository.save(originalInstance);
		} catch (Exception e) {
			log.error(String.format("Illegal input parameter[%s] to update [Permission] object", instance), e);
			return null;
		}
	}

	@Transactional
	@Override
	@CacheEvictBasedCollection(key="#p0")
	public long delete(Long... instanceIds) {
		long count = 0L;
		try {
			if (instanceIds == null) {
				throw new IllegalArgumentException();
			}
			if (!isUsedPermissions(instanceIds)) {
				count = permissionRepository.deleteByIds(instanceIds);
				if (count > 0) RolePermissionsMapping.refill(rolePermissionService.findAllRolePermissionMapping());
			}
		} catch (Exception e) {
			log.error(String.format("Illegal input parameter[%s] to delete [Permission] objects", Arrays.toString(instanceIds)), e);
		}
		return count;
	}
	
	@Override
	@Cacheable(key="#result.id", condition="#result ne null")
	public Permission get(Long instanceId) {
		return permissionRepository.findOne(instanceId);
	}
	
	@CacheableCommonCollection
	@Override
	public List<Permission> get(Long... instanceIds) {
		return null;
	}

	@CacheableBasedPageableCollection
	@Transactional(readOnly=true)
	@Override
	public Page<Permission> get(Pageable pageable) {
		return null;
	}
	
	@CacheableBasedPageableCollection
	@Transactional(readOnly=true)
	@Override
	public Page<Permission> get(Pageable pageable, Predicate predicate) {
		return null;
	}

	@CacheableCommonCollection
	@Override
	public List<Permission> get() {
		return permissionRepository.findAll();
	}
	
	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;
		
//		QPermission permission = QPermission.permission;
		QPermission permission = new QPermission("Permission");
		BooleanExpression exp = null;
		
		String name = conditions.getString(permission.name.getMetadata().getName());
		exp = addExpression(name, exp, permission.name.like(like(name)));
		
		String id = conditions.getString(permission.id.getMetadata().getName());
		exp = addExpression(id, exp, permission.id.eq(toLong(id)));
		
		String action = conditions.getString(permission.action.getMetadata().getName());
		exp = addExpression(action, exp, permission.action.like(like(action)));
		
		String group = conditions.getString(permission.group.getMetadata().getName());
		exp = addExpression(group, exp, permission.group.like(like(group)));
		
		String parentId = conditions.getString(permission.parentId.getMetadata().getName());
		exp = addExpression(parentId, exp, permission.parentId.eq(toLong(parentId)));
		
		String resourceType = conditions.getString(permission.resourceType.getMetadata().getName());
		if (StringUtils.isNotBlank(resourceType))
			exp = addExpression(resourceType, exp, permission.resourceType.eq(resourceType));
		
		String available = conditions.getString(permission.available.getMetadata().getName());
		exp = addExpression(available, exp, permission.available.eq(toBoolean(available)));
		
		return exp;
	}
	
	@CacheableAvailableCollection
	@Override
	public List<Permission> getAllAvailable() {
//		QPermission permission = new QPermission("Permission");
//		return permissionRepository.findByPredicateAndSort(permission.available.eq(true), 
//				new Sort(Direction.ASC, 
//						Arrays.asList(permission.sort.getMetadata().getName(),
//								permission.group.getMetadata().getName(),
//								permission.id.getMetadata().getName())));
		return null;
	}
	
	@Override
	@CachePut(key="#result.id", condition="#result ne null")
	public Permission get(String name) {
		return permissionRepository.findByName(name);
	}
	
	@Override
	public List<Permission> getRelational(Long parentPermissionId) {
		return permissionRepository.getRelational(parentPermissionId);
	}
	
	/**
	 * Determine whether the given permission name is unique in current database table.
	 * @param name - name used to check unique name.
	 * @param instanceId - check unique name excluded the permission instance of the given instance id.
	 * @return if the name is unique (excluded the permission instance of the given instance id) return true, otherwise return false.
	 */
	@Override
	public boolean isPermissionNameUnique(String name, Long instanceId) {
		BooleanExpression predicate = QPermission.permission.name.eq(name);
		if (instanceId != null) predicate = predicate.and(QPermission.permission.id.ne(instanceId));
		return !permissionRepository.exists(predicate);
	}

	/**
	 * Determine whether any of the given instance ids is used in the database table of T_ROLE_PERMISSION.
	 * @param instanceIds - instance ids used to be checked.
	 * @return if any of them used return true, otherwise return false.
	 */
	@Override
	public boolean isUsedPermissions(@NonNull Long... instanceIds) {
		return rolePermissionRepository.exists(QRolePermission.rolePermission.permission.id.in(instanceIds));
	}

	@Override
	public boolean hasPermission(String action) {
		return SpringUtils.getBean(SecurityPermissionEvaluator.class).hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, action);
	}
}
