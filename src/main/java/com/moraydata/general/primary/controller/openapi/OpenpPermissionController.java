package com.moraydata.general.primary.controller.openapi;

import java.util.List;

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

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.Permission;
import com.moraydata.general.primary.service.PermissionService;

@RequestMapping("/open/permission")
@RestController
public class OpenpPermissionController {
	
	@Autowired
	private PermissionService permissionService;
	
	/**
	 * 权限添加
	 * @param permission 新权限信息
	 * @return Permission 添加后的权限信息
	 */
	@PostMapping("/addition")
	public ResponseEntity addition(@RequestBody Permission permission) {
		Assert.notNull(permission, "ADDITION_ROLE_IS_NULL");

		Permission data = permissionService.create(permission);
		if (data == null) {
			return ResponseEntity.UNKNOWN_ERROR;
		} else {
			return ResponseEntity.success("权限添加成功", data);
		}
	}
	
	/**
	 * 获取特定权限信息
	 * @param permissionId 权限编号
	 * @return Permission 获取到的权限信息
	 */
	@GetMapping("single")
	public ResponseEntity single(@RequestParam Long permissionId) {
		Assert.notNull(permissionId, "SINGLE_ROLE_ID_IS_NULL");
		
		try {
			if (permissionId == 0L) {
				return ResponseEntity.error("权限编号有误");
			}
			Permission data =  permissionService.get(permissionId);
			return ResponseEntity.success("获取权限信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定单个或多个权限信息
	 * @param permissionIds 单个serviceId或多个serviceId组成的数组
	 * @return List<Service> 获取到的权限信息集合
	 */
	@GetMapping("multiple")
	public ResponseEntity multiple(@RequestParam Long... permissionIds) {
		Assert.notNull(permissionIds, "MULTIPLE_ROLE_IDS_IS_NULL");
		
		try {
			if (permissionIds.length == 0) {
				return ResponseEntity.error("权限编号集合长度为0");
			}
			List<Permission> data = permissionService.get(permissionIds);
			return ResponseEntity.success("获取权限信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取所有权限信息
	 * @return List<Permission> 获取到的所有权限信息集合
	 */
	@GetMapping("list")
	public ResponseEntity list() {
		try {
			List<Permission> data = permissionService.get();
			return ResponseEntity.success("获取所有权限信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新特定权限信息
	 * @param permission 更新前的权限信息
	 * @return Permission 更新后的权限信息
	 */
	@PutMapping("update")
	public ResponseEntity update(@RequestBody Permission permission) {
		Assert.notNull(permission, "UPDATE_ROLE_IS_NULL");
		
		try {
			Long permissionId = permission.getId();
			if (permissionId == null || permissionId == 0) {
				return ResponseEntity.error("权限编号不存在");
			}
			Permission originalPermission = permissionService.get(permissionId);
			if (originalPermission == null) {
				return ResponseEntity.error("权限不存在");
			}
			Permission data = permissionService.update(permission, originalPermission);
			return ResponseEntity.success("更新权限成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 删除单个或多个权限信息
	 * @param permissionIds 删除单个权限或多个以权限编号为数组的权限信息
	 * @return long 有多少条权限信息被删除了
	 */
	@DeleteMapping("deletion")
	public ResponseEntity deletion(@RequestParam Long... permissionIds) {
		Assert.notNull(permissionIds, "DELETION_ROLE_IDS_IS_NULL");
		
		try {
			if (permissionIds.length == 0) {
				return ResponseEntity.error("权限编号集合长度为0");
			}
			long data = permissionService.delete(permissionIds);
			return ResponseEntity.success("删除权限信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页权限数据
	 * @param conditions 每个key对应属性，每个value对应搜索内容
	 * @param pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<Permission> 查询到的分页数据
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam JSONObject conditions, @RequestParam JSONObject pageable) {
		Assert.notNull(conditions, "PAGE_CONDITIONS_IS_NULL");
		Assert.notNull(pageable, "PAGE_PAGEABLE_IS_NULL");
		
		try {
			Page<Permission> data =  permissionService.get(PageUtils.parsePageable(pageable), permissionService.search(conditions));
			return ResponseEntity.success("获取分页权限信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
    
	/**
	 * 获取所有可用权限列表 
	 * @return List<Permission> 查询到的所有可用权限信息列表
	 */
    @GetMapping("/available")
	public ResponseEntity available() {
    	try {
			List<Permission> data =  permissionService.getAllAvailable();
			return ResponseEntity.success("获取所有可用的权限信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
}
