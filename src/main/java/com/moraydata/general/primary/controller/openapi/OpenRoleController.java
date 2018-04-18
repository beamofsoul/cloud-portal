package com.moraydata.general.primary.controller.openapi;

import static com.moraydata.general.management.util.RegexUtils.match;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.Role;
import com.moraydata.general.primary.entity.UserRoleCombineRole;
import com.moraydata.general.primary.entity.dto.RolePermissionMappingDTO;
import com.moraydata.general.primary.service.RolePermissionService;
import com.moraydata.general.primary.service.RoleService;
import com.moraydata.general.primary.service.UserRoleService;

@RequestMapping("/open/role")
@RestController
public class OpenRoleController {
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserRoleService userRoleSerivce;
	
	@Autowired
	private RolePermissionService rolePermissionService;
	
	/**
	 * 角色添加
	 * @param role 新角色信息
	 * @return Role 添加后的角色信息
	 */
	@PostMapping("/addition")
	public ResponseEntity addition(@RequestBody Role role) {
		Assert.notNull(role, "ADDITION_ROLE_IS_NULL");

		try {
			if (!OpenRoleController.validateName(role.getName())) {
				return ResponseEntity.error("角色名称格式错误");
			}
			if (!roleService.isNameUnique(role.getName(), null)) {
				return ResponseEntity.error("角色名称已被使用");
			}
			if (role.getPriority() <= 0) {
				return ResponseEntity.error("角色优先级格式错误");
			}
			Role data = roleService.create(role);
			return ResponseEntity.success("角色添加成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定角色信息
	 * @param roleId 角色编号
	 * @return Role 获取到的角色信息
	 */
	@GetMapping("single")
	public ResponseEntity single(@RequestParam Long roleId) {
		Assert.notNull(roleId, "SINGLE_ROLE_ID_IS_NULL");
		
		try {
			if (roleId == 0L) {
				return ResponseEntity.error("角色编号有误");
			}
			Role data =  roleService.get(roleId);
			return ResponseEntity.success("获取角色信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定单个或多个角色信息
	 * @param roleIds 单个serviceId或多个serviceId组成的数组
	 * @return List<Service> 获取到的角色信息集合
	 */
	@GetMapping("multiple")
	public ResponseEntity multiple(@RequestParam Long... roleIds) {
		Assert.notNull(roleIds, "MULTIPLE_ROLE_IDS_IS_NULL");
		
		try {
			if (roleIds.length == 0) {
				return ResponseEntity.error("角色编号集合长度为0");
			}
			List<Role> data = roleService.get(roleIds);
			return ResponseEntity.success("获取角色信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取所有角色信息
	 * @return List<Role> 获取到的所有角色信息集合
	 */
	@GetMapping("list")
	public ResponseEntity list() {
		try {
			List<Role> data = roleService.get();
			return ResponseEntity.success("获取所有角色信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新特定角色信息
	 * @param role 更新前的角色信息
	 * @return Role 更新后的角色信息
	 */
	@PutMapping("updating")
	public ResponseEntity update(@RequestBody Role role) {
		Assert.notNull(role, "UPDATE_ROLE_IS_NULL");
		
		try {
			Long roleId = role.getId();
			if (roleId == null || roleId == 0) {
				return ResponseEntity.error("角色编号不存在");
			}
			Role originalRole = roleService.get(roleId);
			if (originalRole == null) {
				return ResponseEntity.error("角色不存在");
			}
			if (!OpenRoleController.validateName(role.getName())) {
				return ResponseEntity.error("角色名称格式错误");
			}
			if (!roleService.isNameUnique(role.getName(), roleId)) {
				return ResponseEntity.error("角色名称已被使用");
			}
			if (role.getPriority() <= 0) {
				return ResponseEntity.error("角色优先级格式错误");
			}
			Role data = roleService.update(role, originalRole);
			return ResponseEntity.success("更新角色成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 删除单个或多个角色信息
	 * @param roleIds 删除单个角色或多个以角色编号为数组的角色信息
	 * @return long 有多少条角色信息被删除了
	 */
	@DeleteMapping("deletion")
	public ResponseEntity deletion(@RequestParam Long... roleIds) {
		Assert.notNull(roleIds, "DELETION_ROLE_IDS_IS_NULL");
		
		try {
			if (roleIds.length == 0) {
				return ResponseEntity.error("角色编号集合长度为0");
			}
			long data = roleService.delete(roleIds);
			return ResponseEntity.success("删除角色信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页角色数据
	 * @param map ->
	 * 				conditions 每个key对应属性，每个value对应搜索内容
	 * 				pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<Role> 查询到的分页数据
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam Map<String, Object> map) {
		Assert.notNull(map, "PAGE_MAP_IS_NULL");
		
		try {
			Page<Role> data =  roleService.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), roleService.search(JSON.parseObject(map.get("conditions").toString())));
			return ResponseEntity.success("获取分页角色信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取多个分页后的用户角色映射信息
	 * @param conditions 每个key对应属性，每个value对应搜索内容
	 * @param pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<UserRoleCombineRole> 查询到的分页数据
	 */
	@GetMapping("/pageOfUserRole")
	public ResponseEntity pageOfUserRole(@RequestParam Map<String, Object> map) {
		Assert.notNull(map, "PAGE_OF_USER_ROLE_MAP_IS_NULL");
		
		try {
			Page<UserRoleCombineRole> data =  userRoleSerivce.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), JSON.parseObject(map.get("conditions").toString()));
			return ResponseEntity.success("获取分页用户角色映射信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 为某个用户更新用户角色映射
	 * @param userRoleCombineRole 更新的用户角色对象
	 * @return UserRoleCombineRole 更新后的用户角色对象
	 */
	@PutMapping("/updateOfUserRole")
	public ResponseEntity updateUserRole(@RequestBody UserRoleCombineRole userRoleCombineRole) {
		Assert.notNull(userRoleCombineRole, "UPDATE_OF_USER_ROLE_USER_ROLE_COMBINE_ROLE_IS_NULL");
		
		try {
			UserRoleCombineRole data = userRoleSerivce.update(userRoleCombineRole);
			return ResponseEntity.success("更新用户角色映射成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
    
	/**
	 * 获取所有可用角色列表 
	 * @return List<Role> 查询到的所有可用角色信息列表
	 */
    @GetMapping("/available")
	public ResponseEntity available() {
    	try {
			List<Role> data =  roleService.getAllAvailable();
			return ResponseEntity.success("获取所有可用的角色信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
    
    /**
     * 更新角色权限映射
     * @param rolePermissionMappingDTO 对应的角色权限映射信息DTO
     * @return boolean 是否更新成功
     */
	@PutMapping(value = "/allotPermission")
    public ResponseEntity allotPermission(@RequestBody RolePermissionMappingDTO rolePermissionMappingDTO) {
		Assert.notNull(rolePermissionMappingDTO, "ALLOT_PERMISSION_ROLE_PERMISSION_MAPPING_DTO_IS_NULL");
		
		try {
			boolean data = rolePermissionService.updateRolePermissionMapping(rolePermissionMappingDTO);
			return ResponseEntity.success("分配角色权限成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
    }

	/**
	 * 刷新用户角色映射关系
	 */
    @GetMapping("/refreshRolePermissionMapping")
    public void refreshRolePermissionMapping() {
        rolePermissionService.refreshRolePermissionMapping();
    }
    
    static boolean validateName(String name) {
		String regex = "^[\\u4E00-\\u9FA5a-zA-Z][\\u4E00-\\u9FA5a-zA-Z0-9]{2,20}$";
		return match(name, regex);
	}
}
