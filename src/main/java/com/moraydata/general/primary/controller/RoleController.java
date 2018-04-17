package com.moraydata.general.primary.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.JSONUtils;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.primary.entity.Role;
import com.moraydata.general.primary.entity.UserRoleCombineRole;
import com.moraydata.general.primary.entity.dto.RolePermissionMappingDTO;
import com.moraydata.general.primary.service.RolePermissionService;
import com.moraydata.general.primary.service.RoleService;
import com.moraydata.general.primary.service.UserRoleService;

import io.swagger.annotations.ApiOperation;

/**
 * @ClassName RoleController
 * @Description Restful controller for role module and other APIs
 * @author MingshuJian
 * @Date 2017年8月29日 上午10:52:08
 * @version 1.0.0
 */
@RestController
@RequestMapping("/admin/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleSerivce;

    @Resource
    private RolePermissionService rolePermissionService;

    @ApiOperation(value = "创建角色", notes = "根据Role对象创建角色")
    @PreAuthorize("authenticated and hasPermission('role','role:add')")
	@PostMapping("/add")
    public Role createRole(@RequestBody Role role) {
        return roleService.create(role);
    }
    
    @ApiOperation(value="获取所有角色详细信息", notes="获取角色信息列表")
	@GetMapping("/list")
	public List<Role> getAllRoles() {
		return roleService.get();
	}
    
    @ApiOperation(value="获取角色详细信息列表", notes="根据一组角色ID获取对应的角色信息列表")
	@GetMapping("/list/{ids}")
	public List<Role> getRoles(@PathVariable Long... ids) {
		return roleService.get(ids);
	}
    
    @ApiOperation(value = "更新角色", notes = "根据Role对象更新角色")
    @PreAuthorize("authenticated and hasPermission('role','role:update')")
	@PutMapping("/update")
	public Role updateRole(@RequestBody Role role) {
		return roleService.update(role);
	}
    
    @ApiOperation(value="删除一个或多个角色", notes="根据一个或多个角色ID删除对应的角色信息")
    @PreAuthorize("authenticated and hasPermission('role','role:delete')")
    @DeleteMapping("/delete/{ids}")
	public long deleteRoles(@PathVariable Long... ids) {
		return roleService.delete(ids);
	}
    
    /***************************************************************************/
    
    @ApiOperation(value="分页查询多个角色", notes="根据一个或多个查询条件查询对应的分页角色信息")
    @PreAuthorize("authenticated and hasPermission('role','role:list')")
	@GetMapping("/page")
	public Page<Role> getPageableRoles(@RequestParam Map<String, Object> params) {
		JSONObject jsonParams = JSONObject.parseObject(JSON.toJSONString(params));
		Object conditions = jsonParams.get("conditions");
		return roleService.get(PageUtils.parsePageable(params), 
				roleService.search(JSONUtils.formatAndParseObject(conditions)));
	}
    
    @ApiOperation(value="分页查询多个用户角色映射", notes="根据一个或多个查询条件查询对应的分页用户角色映射信息")
    @PreAuthorize("authenticated and hasPermission('role','role:roleuser')")
	@GetMapping("/pageOfUserRole")
	public Page<UserRoleCombineRole> getPageableUserRoles(@RequestParam Map<String, Object> params) {
		JSONObject jsonParams = JSONObject.parseObject(JSON.toJSONString(params));
		return userRoleSerivce.get(PageUtils.parsePageable(params), 
				JSONUtils.formatAndParseObject(jsonParams.get("conditions")));
	}
    
    @ApiOperation(value="查询所有可用的角色信息", notes="查询所有Available属性为true的角色信息")
    @GetMapping("/available")
	public List<Role> getAllAvailable() {
		return roleService.getAllAvailable();
	}
    
	@ApiOperation(value="获取某个角色详细信息", notes="根据一个角色ID获取对应的角色信息")
	@GetMapping("/single/{roleId}")
	public Role getRole(@PathVariable long roleId) {
		return roleService.get(roleId);
	}
	
	@ApiOperation(value="获取某个用户角色映射详细信息", notes="根据一个用户角色映射ID获取对应的用户角色映射信息")
    @PreAuthorize("authenticated and hasPermission('role','role:roleuser')")
	@GetMapping("/singleOfUserRole/{userRoleId}")
	public UserRoleCombineRole getUserRole(@PathVariable long userRoleId) {
		return userRoleSerivce.get(userRoleId);
	}

	@ApiOperation(value = "更新用户角色映射", notes = "根据UserRoleCombineRole对象更新用户角色映射")
    @PreAuthorize("authenticated and hasPermission('role','role:roleuser')")
	@PutMapping("/updateOfUserRole")
	public UserRoleCombineRole updateUserRole(@RequestBody UserRoleCombineRole userRoleCombineRole) {
		return userRoleSerivce.update(userRoleCombineRole);
	}
	
	@ApiOperation(value="判断某个角色名称是否唯一", notes="根据一个角色名称判断是否该角色名称唯一(如果同时接收到一个角色ID, 则在判断角色名称唯一的过程中排除给定的角色ID所指向的角色名称)")
	@GetMapping("/isRoleNameUnique")
    public boolean isRoleNameUnique(@RequestParam Map<String, Object> params) {
        String roleName = params.get("uniqueData").toString();
        Long roleId = params.containsKey(Constants.ENTITY.DEFAULT_PRIMARY_KEY) ? 
				Long.valueOf(params.get(Constants.ENTITY.DEFAULT_PRIMARY_KEY).toString()) : 
				null;
        return roleService.isNameUnique(roleName, roleId);
    }

	@ApiOperation(value="判断某些角色是否已经被用户使用", notes="根据一个或多个角色ID判断是否该角色ID所对应的用户角色映射被使用过")
	@GetMapping("/isUsedRoles/{roleIds}")
    public boolean isUsedRoles(@PathVariable Long... roleIds) {
        return roleService.isUsedRoles(roleIds);
    }

	@ApiOperation(value = "更新角色权限映射", notes = "根据RolePermissionMappingDTO对象更新角色权限映射")
	@PreAuthorize("authenticated and hasPermission('role','role:rolepermission')")
	@PutMapping(value = "/allot")
    public boolean updateOfRolePermission(@RequestBody RolePermissionMappingDTO rolePermissionMappingDTO) {
        return rolePermissionService.updateRolePermissionMapping(rolePermissionMappingDTO);
    }

    @ApiOperation(value="刷新角色权限映射关系", notes="刷新系统角色权限映射关系")
    @GetMapping("/refreshMapping")
    public void refreshMapping() {
        rolePermissionService.refreshRolePermissionMapping();
    }
}
