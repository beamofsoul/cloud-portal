package com.moraydata.general.primary.service;

import java.util.Collection;
import java.util.List;

import com.moraydata.general.primary.entity.RolePermission;
import com.moraydata.general.primary.entity.dto.RolePermissionDTO;
import com.moraydata.general.primary.entity.dto.RolePermissionMappingDTO;

public interface RolePermissionService {

	List<RolePermissionDTO> findAllRolePermissionMapping();
	boolean updateRolePermissionMapping(RolePermissionMappingDTO rolePermissionMappingDTO);
	
	Collection<RolePermission> create(Collection<RolePermission> rolePermissions);
	Long delete(Long roleId, Collection<Long> permissionIds);
	void refreshRolePermissionMapping();
}
