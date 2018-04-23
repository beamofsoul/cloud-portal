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
import com.moraydata.general.primary.entity.Order;
import com.moraydata.general.primary.entity.OrderItem;
import com.moraydata.general.primary.service.OrderItemService;

@RequestMapping("/open/orderItem")
@RestController
public class OpenOrderItemController {
	
	@Autowired
	private OrderItemService orderItemService;
	
	/**
	 * 获取特定订单细则信息
	 * @param orderItemId 订单细则编号
	 * @return OrderItem 获取到的订单细则信息
	 */
	@GetMapping("single")
	public ResponseEntity single(@RequestParam Long orderItemId) {
		Assert.notNull(orderItemId, "SINGLE_ORDER_ITEM_ID_IS_NULL");
		
		try {
			if (orderItemId == 0L) {
				return ResponseEntity.error("订单细则编号有误");
			}
			OrderItem data =  orderItemService.get(orderItemId);
			return ResponseEntity.success("获取订单细则信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定单个或多个订单细则信息
	 * @param orderItemIds 单个orderItemId或多个orderItemId组成的数组
	 * @return List<OrderItem> 获取到的订单细则信息集合
	 */
	@GetMapping("multiple")
	public ResponseEntity multiple(@RequestParam Long... orderItemIds) {
		Assert.notNull(orderItemIds, "MULTIPLE_ORDER_ITEM_IDS_IS_NULL");
		
		try {
			if (orderItemIds.length == 0) {
				return ResponseEntity.error("订单细则编号集合长度为0");
			}
			List<OrderItem> data = orderItemService.get(orderItemIds);
			return ResponseEntity.success("获取订单细则信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取所有订单细则信息
	 * @return List<OrderItem> 获取到的所有订单细则信息集合
	 */
	@GetMapping("list")
	public ResponseEntity list() {
		try {
			List<OrderItem> data = orderItemService.get();
			return ResponseEntity.success("获取所有订单细则信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定用户的所有订单细则信息
	 * @param userId 特定的用户Id
	 * @return List<OrderItem> 获取到的订单细则信息列表
	 */
	@GetMapping("all")
	public ResponseEntity all(@RequestParam Long userId) {
		Assert.notNull(userId, "ALL_USER_ID_IS_NULL");
		
		try {
			List<OrderItem> list = orderItemService.getByUserId(userId);
			return ResponseEntity.success("获取所有特定用户订单细节信息成功", list);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页订单细则数据
	 * @param map ->
	 * 				conditions 每个key对应属性，每个value对应搜索内容
	 * 				pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<Order> 查询到的分页数据
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam Map<String, Object> map) {
		Assert.notNull(map, "PAGE_MAP_IS_NULL");
		
		try {
			Page<OrderItem> data =  orderItemService.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), orderItemService.search(JSON.parseObject(map.get("conditions").toString())));
			return ResponseEntity.success("获取分页订单信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 删除单个或多个订单细则信息
	 * @param orderItemIds 删除单个订单细则或多个以订单细则编号为数组的订单细则信息
	 * @return long 有多少条订单细则信息被删除了
	 */
	@DeleteMapping("deletion")
	public ResponseEntity deletion(@RequestParam Long... orderItemIds) {
		Assert.notNull(orderItemIds, "DELETION_ORDER_ITEM_IDS_IS_NULL");
		
		try {
			if (orderItemIds.length == 0) {
				return ResponseEntity.error("订单细则编号数组长度为0");
			}
			long data = orderItemService.delete(orderItemIds);
			return ResponseEntity.success("删除订单细则信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 订单细则添加
	 * @param orderItem 新订单细则信息
	 * @return Order 添加后的订单细致信息
	 */
	@PostMapping("/addition")
	public ResponseEntity addition(@RequestBody OrderItem orderItem) {
		Assert.notNull(orderItem, "ADDITION_ORDER_ITEM_IS_NULL");
		
		try {
			if (!(orderItem.getUserId() != null && orderItem.getUserId() > 0)) {
				return ResponseEntity.error("用户编号格式错误");
			}
			if (orderItem.getServiceId() == null || orderItem.getServiceId() <= 0) {
				return ResponseEntity.error("具体服务格式错误");
			}
			if (StringUtils.isNotBlank(orderItem.getDescription())) {
				if (!validateSize(orderItem.getDescription(), 100)) {
					return ResponseEntity.error("备注格式错误");
				}
			}
			if (orderItem.getServiceBeginTime() == null) {
				return ResponseEntity.error("服务开始时间格式错误");
			}
			if (orderItem.getServiceEndTime() == null) {
				return ResponseEntity.error("服务结束时间格式错误");
			}
			if (orderItem.getStatus() == null || !Order.Status.exists(orderItem.getStatus().intValue())) {
				return ResponseEntity.error("订单细则状态格式错误");
			}
			OrderItem data = orderItemService.create(orderItem);
			return ResponseEntity.success("订单细则添加成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新特定订单细则信息
	 * @param orderItem 更新前的订单细则信息
	 * @return Order 更新后的订单细则信息
	 */
	@PutMapping("updating")
	public ResponseEntity updating(@RequestBody OrderItem orderItem) {
		Assert.notNull(orderItem, "UPDATE_ORDER_ITEM_IS_NULL");
		
		try {
			if (!(orderItem.getUserId() != null && orderItem.getUserId() > 0)) {
				return ResponseEntity.error("用户编号格式错误");
			}
			if (orderItem.getServiceId() == null || orderItem.getServiceId() <= 0) {
				return ResponseEntity.error("具体服务格式错误");
			}
			if (StringUtils.isNotBlank(orderItem.getDescription())) {
				if (!validateSize(orderItem.getDescription(), 100)) {
					return ResponseEntity.error("备注格式错误");
				}
			}
			if (orderItem.getServiceBeginTime() == null) {
				return ResponseEntity.error("服务开始时间格式错误");
			}
			if (orderItem.getServiceEndTime() == null) {
				return ResponseEntity.error("服务结束时间格式错误");
			}
			if (orderItem.getStatus() == null || !Order.Status.exists(orderItem.getStatus().intValue())) {
				return ResponseEntity.error("订单状态格式错误");
			}
			Long orderItemId = orderItem.getId();
			if (orderItemId == null || orderItemId == 0) {
				return ResponseEntity.error("订单细则编号不存在");
			}
			OrderItem originalOrderItem = orderItemService.get(orderItemId);
			if (originalOrderItem == null) {
				return ResponseEntity.error("订单细则不存在");
			}
			OrderItem data = orderItemService.update(orderItem, originalOrderItem);
			return ResponseEntity.success("更新订单细则成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新订单细则状态
	 * @param orderItemId 订单细则编号
	 * @param status 订单细则状态
	 * @return boolean 是否更新成功
	 */
	@PutMapping("updatingStatus")
	public ResponseEntity udpatingStatus(@RequestParam Long orderItemId, @RequestParam Integer status) {
		Assert.notNull(orderItemId, "UPDATING_STATUS_ORDER_ID_IS_NULL");
		Assert.notNull(status, "UPDATING_STATUS_STATUS_IS_NULL");
		
		try {
			if (status == null || !Order.Status.exists(status.intValue())) {
				return ResponseEntity.error("订单细则状态格式错误");
			}
			if (orderItemId == 0) {
				return ResponseEntity.error("订单细则编号格式错误");
			}
			boolean data = orderItemService.updateStatus(orderItemId, Order.Status.getInstance(status));
			return ResponseEntity.success("更新订单状态成功", data);
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
