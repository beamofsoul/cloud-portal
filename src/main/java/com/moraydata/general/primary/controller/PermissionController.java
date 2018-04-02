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
import com.moraydata.general.primary.entity.Permission;
import com.moraydata.general.primary.service.PermissionService;

import io.swagger.annotations.ApiOperation;

/**
 * @ClassName PermissionController
 * @Description Restful controller for permission module and other APIs
 * @author MingshuJian
 * @Date 2017年8月29日 上午10:52:08
 * @version 1.0.0
 */
@RestController
@RequestMapping("/admin/permission")
public class PermissionController {

	@Resource
	private PermissionService permissionService;
	
    @ApiOperation(value = "创建权限", notes = "根据Permission对象创建权限")
    @PreAuthorize("authenticated and hasPermission('permission','permission:add')")
	@PostMapping("/add")
    public Permission createPermission(@RequestBody Permission permission) {
        return permissionService.create(permission);
    }
    
    @ApiOperation(value="获取所有权限详细信息", notes="获取权限信息列表")
	@GetMapping("/list")
	public List<Permission> getAllPermissions() {
		return permissionService.get();
	}
	
    @ApiOperation(value="获取权限详细信息列表", notes="根据一组权限ID获取对应的权限信息列表")
	@GetMapping("/list/{ids}")
	public List<Permission> getPermissions(@PathVariable Long... ids) {
		return permissionService.get(ids);
	}
    
    @ApiOperation(value="获取某个特定权限详细信息", notes="根据一个权限ID获取对应的权限信息")
	@GetMapping("/single/{id}")
	public Permission getPermissions(@PathVariable Long id) {
		return permissionService.get(id);
	}
    
    @ApiOperation(value = "更新权限", notes = "根据Permission对象更新权限")
    @PreAuthorize("authenticated and hasPermission('permission','permission:update')")
	@PutMapping("/update")
	public Permission updatePermission(@RequestBody Permission permission) {
		return permissionService.update(permission);
	}
    
    @ApiOperation(value="删除一个或多个权限", notes="根据一个或多个权限ID删除对应的权限信息")
    @PreAuthorize("authenticated and hasPermission('permission','permission:delete')")
    @DeleteMapping("/delete/{ids}")
	public long deletePermissions(@PathVariable Long... ids) {
		return permissionService.delete(ids);
	}
    
    /***************************************************************************/
    
    @ApiOperation(value="分页查询多个权限", notes="根据一个或多个查询条件查询对应的分页权限信息")
    @PreAuthorize("authenticated and hasPermission('permission','permission:list')")
	@GetMapping("/page")
    public Page<Permission> getPageablePermissions(@RequestParam Map<String, Object> params) {
		JSONObject jsonParams = JSONObject.parseObject(JSON.toJSONString(params));
		Object conditions = jsonParams.get("conditions");
		return permissionService.get(PageUtils.parsePageable(params), 
				permissionService.search(JSONUtils.formatAndParseObject(conditions)));
	}
    
    @ApiOperation(value="分页查询多个子节点权限", notes="根据父节点ID查询多个对应的子节点权限信息")
    @PreAuthorize("authenticated and hasPermission('permission','permission:list')")
	@GetMapping("/listOfChildren/{parentPermissionId}")
    public List<Permission> getChildrenPermissions(@PathVariable Long parentPermissionId) {
		return permissionService.getRelational(permissionService.searchRelational(parentPermissionId));
	}
	
    @ApiOperation(value="查询所有可用的权限信息", notes="查询所有Available属性为true的权限信息")
    @GetMapping("/available")
	public List<Permission> getAllAvailable() {
		return permissionService.getAllAvailable();
	}
    
    @ApiOperation(value="判断某个权限名称是否唯一", notes="根据一个权限名称判断是否该权限名称唯一(如果同时接收到一个权限ID, 则在判断权限名称唯一的过程中排除给定的权限ID所指向的权限名称)")
	@GetMapping("/isPermissionNameUnique")
    public boolean isPermissionNameUnique(@RequestParam Map<String, Object> params) {
        String permissionName = params.get("uniqueData").toString();
        Long permissionId = params.containsKey(Constants.ENTITY.DEFAULT_PRIMARY_KEY) ? 
				Long.valueOf(params.get(Constants.ENTITY.DEFAULT_PRIMARY_KEY).toString()) : 
				null;
        return permissionService.isPermissionNameUnique(permissionName, permissionId);
    }
    
    @ApiOperation(value="判断某些权限是否已经被使用", notes="根据一个或多个权限ID判断是否该权限ID所对应的权限是否被使用过")
	@GetMapping("/isUsedPermissions/{permissionIds}")
    public boolean isUsedPermissions(@PathVariable Long... permissionIds) {
        return permissionService.isUsedPermissions(permissionIds);
    }

    @ApiOperation(value="判断当前用户的角色是否拥有某个权限动作", notes="根据一个权限动作判断是否当前用户的角色拥有该权限动作对应的权限")
    @GetMapping("/hasPermission/{action}")
	public boolean hasPermission(@RequestBody Object action) {
    	return permissionService.hasPermission(action.toString());
	}
}
