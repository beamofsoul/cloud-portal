package com.moraydata.general.primary.controller.openapi;

import static com.moraydata.general.management.util.RegexUtils.match;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.moraydata.general.primary.entity.Service;
import com.moraydata.general.primary.service.ServiceService;

@RequestMapping("/open/service")
@RestController
public class OpenServiceController {
	
	@Autowired
	private ServiceService serviceService;
	
	/**
	 * 服务添加
	 * @param service 新服务信息
	 * @return Service 添加后的服务信息
	 */
	@PostMapping("/addition")
	public ResponseEntity addition(@RequestBody Service service) {
		Assert.notNull(service, "ADDITION_SERVICE_IS_NULL");
		
		try {
			if (!OpenServiceController.validateSize(service.getName(), 50)) {
				return ResponseEntity.error("服务名称格式错误");
			}
			Service data = serviceService.create(service);
			return ResponseEntity.success("服务添加成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定服务信息
	 * @param serviceId 服务编号
	 * @return Service 获取到的服务信息
	 */
	@GetMapping("single")
	public ResponseEntity single(@RequestParam Long serviceId) {
		Assert.notNull(serviceId, "SINGLE_SERVICE_ID_IS_NULL");
		
		try {
			if (serviceId == 0L) {
				return ResponseEntity.error("服务编号有误");
			}
			Service data =  serviceService.get(serviceId);
			return ResponseEntity.success("获取服务信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定单个或多个服务信息
	 * @param serviceIds 单个serviceId或多个serviceId组成的数组
	 * @return List<Service> 获取到的服务信息集合
	 */
	@GetMapping("multiple")
	public ResponseEntity multiple(@RequestParam Long... serviceIds) {
		Assert.notNull(serviceIds, "MULTIPLE_SERVICE_IDS_IS_NULL");
		
		try {
			if (serviceIds.length == 0) {
				return ResponseEntity.error("服务编号集合长度为0");
			}
			List<Service> data = serviceService.get(serviceIds);
			return ResponseEntity.success("获取服务信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取所有服务信息
	 * @return List<Service> 获取到的所有服务信息集合
	 */
	@GetMapping("list")
	public ResponseEntity list() {
		try {
			List<Service> data = serviceService.get();
			return ResponseEntity.success("获取所有服务信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新特定服务信息
	 * @param service 更新前的服务信息
	 * @return Service 更新后的服务信息
	 */
	@PutMapping("updating")
	public ResponseEntity updating(@RequestBody Service service) {
		Assert.notNull(service, "UPDATE_SERVICE_IS_NULL");
		
		try {
			if (!OpenServiceController.validateSize(service.getName(), 50)) {
				return ResponseEntity.error("服务名称格式错误");
			}
			Long serviceId = service.getId();
			if (serviceId == null || serviceId == 0) {
				return ResponseEntity.error("服务编号不存在");
			}
			Service originalService = serviceService.get(serviceId);
			if (originalService == null) {
				return ResponseEntity.error("服务不存在");
			}
			Service data = serviceService.update(service, originalService);
			return ResponseEntity.success("更新服务成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 删除单个或多个服务信息
	 * @param serviceIds 删除单个服务或多个以服务编号为数组的服务信息
	 * @return long 有多少条服务信息被删除了
	 */
	@DeleteMapping("deletion")
	public ResponseEntity deletion(@RequestParam Long... serviceIds) {
		Assert.notNull(serviceIds, "DELETION_SERVICE_IDS_IS_NULL");
		
		try {
			if (serviceIds.length == 0) {
				return ResponseEntity.error("服务编号集合长度为0");
			}
			if (serviceService.isUsedService(serviceIds)) {
				return ResponseEntity.error("当前服务正被使用中");
			}
			long data = serviceService.delete(serviceIds);
			return ResponseEntity.success("删除服务信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页服务数据
	 * @param map ->
	 * 				conditions 每个key对应属性，每个value对应搜索内容
	 * 				pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<Service> 查询到的分页数据
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam Map<String, Object> map) {
		Assert.notNull(map, "PAGE_MAP_IS_NULL");
		
		try {
			Page<Service> data =  serviceService.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), serviceService.search(JSON.parseObject(map.get("conditions").toString())));
			return ResponseEntity.success("获取分页服务信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	static boolean validateSize(String string, int size) {
		if (StringUtils.isBlank(string)) {
			return false;
		}
		String regex = String.format("^.{1,%s}$", size);
		return match(string, regex);
	}
}
