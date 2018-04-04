package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toBoolean;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toInteger;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLocalDateTime;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.Role;
import com.moraydata.general.primary.entity.query.QRole;
import com.moraydata.general.primary.entity.query.QUserRole;
import com.moraydata.general.primary.repository.RoleRepository;
import com.moraydata.general.primary.repository.UserRoleRepository;
import com.moraydata.general.primary.service.RoleService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service("roleService")
//@CacheConfig(cacheNames = "roleCache")
public class RoleServiceImpl extends BaseAbstractService implements RoleService {
	
//	public static final String CACHE_NAME = "roleCache";

	@Autowired
	private RoleRepository roleRepository;
	
//	@Autowired
//	private RolePermissionService rolePermissionService;
//	
	@Autowired
	private UserRoleRepository userRoleRepository;

	@Override
//	@CachePut(key="#result.id")
	public Role create(Role instance) {
		return roleRepository.save(instance);
	}
	
	@Transactional
//	@CachePut(key="#role.id")
	@Override
	public Role update(Role instance) {
		Role originalInstance = get(instance.getId());
		BeanUtils.copyProperties(instance, originalInstance);
		return roleRepository.save(originalInstance);
	}
	
	@Transactional
	@Override
	public long delete(Long... instanceIds) {
		// Determine whether any of the instance ids has been related with user instances.
		// If so, do not delete any of them, just return 0 which means delete operation failed.
		if (isUsedRoles(instanceIds)) {
			return 0;
		}
		return roleRepository.deleteByIds(instanceIds);
	}

//	@Override
////	@CacheEvictBasedCollection(key="#p0")
//	@Transactional
//	public long delete(@NonNull Long... ids) {
//		try {
//			if (ids.length > 0) {
//				long count = roleRepository.deleteByIds(ids);
//				if (count > 0) 
//					RolePermissionsMapping.refill(rolePermissionService.
//							findAllRolePermissionMapping());
//				return count;
//			} else {
//				throw new Exception("failed to delete roles because zero-length of role ids");
//			}
//		} catch (Exception e) {
//			logger.error("role ids must be not zero-length when deleting...", e);
//		}
//		return 0;
//	}
	
	@Override
	public Role get(Long instanceId) {
		return roleRepository.findOne(instanceId); 
	}

	@Override
	public List<Role> get(Long... instanceIds) {
		return roleRepository.findByIds(instanceIds);
	}

	@Override
	public Page<Role> get(Pageable pageable) {
		return roleRepository.findAll(pageable);
	}

	@Override
	public Page<Role> get(Pageable pageable, Predicate predicate) {
		return roleRepository.findAll(predicate, pageable);
	}

	@Override
	public List<Role> get() {
		return roleRepository.findAll();
	}
	
	@Override
	public List<Role> getAllAvailable() {
		QRole role = new QRole("Role");
		return roleRepository.findByPredicateAndSort(role.available.eq(true), 
				new Sort(Direction.ASC, 
						Arrays.asList(role.priority.getMetadata().getName(),
								role.id.getMetadata().getName())));
	}

	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;

		QRole role = QRole.role;
		BooleanExpression exp = null;
		
		String id = conditions.getString(role.id.getMetadata().getName());
		exp = addExpression(id, exp, role.id.eq(toLong(id)));
		
		String name = conditions.getString(role.name.getMetadata().getName());
		exp = addExpression(name, exp, role.name.like(like(name)));
		
		String priority = conditions.getString(role.priority.getMetadata().getName());
		exp = addExpression(priority, exp, role.priority.eq(toInteger(priority)));
		
		
		String available = conditions.getString(role.available.getMetadata().getName());
		exp = addExpression(available, exp, role.available.eq(toBoolean(available)));
		
		String createdDate = conditions.getString(role.createdDate.getMetadata().getName());
		exp = addExpression(createdDate, exp, role.createdDate.before(toLocalDateTime(createdDate)));
		
		return exp;
	}
	
	/**
	 * Get unique role instance by name, otherwise return a null value.
	 * @see AuthenticationUserDetailsService#getUser0
	 * @param name - name used to get unique role instance.
	 * @return an instance of role have gotten or a null value. 
	 */
	@Override
	public Role get(String name) {
		return roleRepository.findByName(name);
	}

//	@Override
//	public List<Role> findAll() {
//		return roleRepository.findAll();
//	}
//
//	@Override
////	@CachePut(key="#result.id")
//	public Role findByName(String name) {
//		return roleRepository.findByName(name);
//	}
//	
//	@Override
////	@Cacheable(key="#id")
//	public Role findById(Long id) {
//		Role role = roleRepository.findOne(id);
//		if (role != null) Hibernate.initialize(role.getPermissions());
//		return role;
//	}
//
//	@Override
////	@CacheableBasedPageableCollection(entity = Role.class)
//	@Transactional(readOnly=true)
//	public Page<Role> findAll(Pageable pageable) {
//		return null;
//	}
//
//	@Override
////	@CacheableBasedPageableCollection(entity = Role.class)
//	@Transactional(readOnly=true)
//	public Page<Role> findAll(Pageable pageable, Predicate predicate) {
//		return null;
//	}

	@Override
	public boolean isRoleNameUnique(String roleName, Long roleId) {
		BooleanExpression predicate = QRole.role.name.eq(roleName);
		if (roleId != null) predicate = predicate.and(QRole.role.id.ne(roleId));
		return !roleRepository.exists(predicate);
	}

	@Override
	public boolean isUsedRoles(Long... roleIds) {
		return userRoleRepository.exists(new QUserRole("userRole").role.id.in(roleIds));
	}
}
