package com.moraydata.general.primary.controller.openapi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.management.util.ResponseEntity;
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
	 * @param conditions 每个key对应属性，每个value对应搜索内容
	 * @param pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<OrderItem>
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam JSONObject conditions, @RequestParam JSONObject pageable) {
		Assert.notNull(conditions, "PAGE_CONDITIONS_IS_NULL");
		Assert.notNull(pageable, "PAGE_PAGEABLE_IS_NULL");
		
		try {
			Page<OrderItem> data =  orderItemService.get(PageUtils.parsePageable(pageable), orderItemService.search(conditions));
			return ResponseEntity.success("获取分页订单细则信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
}
