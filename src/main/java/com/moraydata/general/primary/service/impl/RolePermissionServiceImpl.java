package com.moraydata.general.primary.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moraydata.general.management.system.RolePermissionsMapping;
import com.moraydata.general.management.util.CacheUtils;
import com.moraydata.general.management.util.CollectionUtils;
import com.moraydata.general.primary.entity.RolePermission;
import com.moraydata.general.primary.entity.dto.RolePermissionDTO;
import com.moraydata.general.primary.entity.dto.RolePermissionMappingDTO;
import com.moraydata.general.primary.entity.query.QPermission;
import com.moraydata.general.primary.entity.query.QRole;
import com.moraydata.general.primary.repository.RolePermissionRepository;
import com.moraydata.general.primary.service.PermissionService;
import com.moraydata.general.primary.service.RolePermissionService;
import com.moraydata.general.primary.service.RoleService;

@Service("rolePermissionService")
public class RolePermissionServiceImpl extends BaseAbstractService implements RolePermissionService {

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	
	@Override
	public List<RolePermissionDTO> findAllRolePermissionMapping() {
		return rolePermissionRepository.findAllRolePermissionMapping();
	}

	@Transactional
	@Override
	public boolean updateRolePermissionMapping(RolePermissionMappingDTO dto) {
		try {
			// 1. get the original role permission mappings
			final List<Long> originalIdList = rolePermissionRepository.findByPredicate(QRole.role.id.eq(dto.getRoleId())).stream().map(e -> e.getPermission().getId()).collect(Collectors.toList());
			final Long roleId = dto.getRoleId();
			final Set<Long> permissionIds = dto.getPermissionIds();
			// 2. get all mappings which should be added
			List<RolePermission> neededList = permissionIds.stream().filter(e -> !originalIdList.contains(e)).map(e -> new RolePermission(roleService.get(roleId), permissionService.get(e))).collect(Collectors.toList());
			// 3. get all mappings which should be removed
			List<Long> abandonedList = originalIdList.stream().filter(e -> !permissionIds.contains(e)).collect(Collectors.toList());
			// 4. execute adding and removing operations
			if (CollectionUtils.isNotBlank(neededList)) this.create(neededList);
			if (CollectionUtils.isNotBlank(abandonedList)) this.delete(roleId, abandonedList);
			// 5. clear current role's cache
			CacheUtils.remove("roleCache", roleId); 
			// 6. refresh role permission mappings
			this.refreshRolePermissionMapping();
			// 7. return updating result
			return true;
		} catch (Exception e) {
			logger.error("failed to update the mappings between role and permission entity", e);
			return false;
		}
	}
	
	@Override
	public Collection<RolePermission> create(Collection<RolePermission> rolePermissions) {
		return rolePermissionRepository.saveAll(rolePermissions);
	}
	
	@Override
	public Long delete(Long roleId, Collection<Long> permissionIds) {
		// An exception will occur once use "QRolePermission.role.id" or "QRolePermission.permission.id"
		// It looks like an incompatibility bug of QueryDSL and Hibernate or a problem caused by generated QueryDSL domain type file
		// It has been reported to QueryDSL project team on GitHub, the URL is : https://github.com/querydsl/querydsl/issues/2102
		return rolePermissionRepository.deleteByPredicate(QRole.role.id.eq(roleId).and(QPermission.permission.id.in(permissionIds)));
	}

	@Override
	public void refreshRolePermissionMapping() {
		RolePermissionsMapping.refill(this.findAllRolePermissionMapping());
	}
}
