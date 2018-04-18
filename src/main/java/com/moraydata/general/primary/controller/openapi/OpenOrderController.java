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
import com.moraydata.general.primary.service.OrderService;

@RequestMapping("/open/order")
@RestController
public class OpenOrderController {
	
	@Autowired
	private OrderService orderService;
	
	/**
	 * 订单添加
	 * @param order 新订单信息
	 * @return Order 添加后的订单信息
	 */
	@PostMapping("/addition")
	public ResponseEntity addition(@RequestBody Order order) {
		Assert.notNull(order, "ADDITION_ORDER_IS_NULL");
		
		try {
			if (!(order.getUserId() != null && order.getUserId() > 0)) {
				return ResponseEntity.error("用户编号格式错误");
			}
			if (StringUtils.isNotBlank(order.getCode())) {
				return ResponseEntity.error("订单编号无需填写");
			}
			if (order.getAmount().doubleValue() < 0) {
				return ResponseEntity.error("订单金额格式错误");
			}
			if (StringUtils.isNotBlank(order.getAgent())) {
				if (!validateSize(order.getAgent(), 100)) {
					return ResponseEntity.error("代理商格式错误");
				}
			}
			if (order.getAmountForAgent().doubleValue() < 0) {
				return ResponseEntity.error("代理商结算金额格式错误");
			}
			if (StringUtils.isNotBlank(order.getOperator())) {
				if (!validateSize(order.getOperator(), 50)) {
					return ResponseEntity.error("运维人员格式错误");
				}
			}
			if (StringUtils.isBlank(order.getServiceIds())) {
				return ResponseEntity.error("具体服务格式错误");
			}
			if (StringUtils.isNotBlank(order.getDescription())) {
				if (!validateSize(order.getDescription(), 100)) {
					return ResponseEntity.error("备注格式错误");
				}
			}
			if (order.getServiceBeginTime() == null) {
				return ResponseEntity.error("服务开始时间格式错误");
			}
			if (order.getServiceEndTime() == null) {
				return ResponseEntity.error("服务结束时间格式错误");
			}
			if (order.getStatus() == null || !Order.Status.exists(order.getStatus().intValue())) {
				return ResponseEntity.error("订单状态格式错误");
			}
			order.setCode(String.valueOf(orderService.getNextOrderCode()));
			Order data = orderService.create(order);
			return ResponseEntity.success("订单添加成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定订单信息
	 * @param orderId 订单编号
	 * @return Order 获取到的订单信息
	 */
	@GetMapping("single")
	public ResponseEntity single(@RequestParam Long orderId) {
		Assert.notNull(orderId, "SINGLE_ORDER_ID_IS_NULL");
		
		try {
			if (orderId == 0L) {
				return ResponseEntity.error("订单编号有误");
			}
			Order data =  orderService.get(orderId);
			return ResponseEntity.success("获取订单信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定单个或多个订单信息
	 * @param orderIds 单个serviceId或多个serviceId组成的数组
	 * @return List<Service> 获取到的订单信息集合
	 */
	@GetMapping("multiple")
	public ResponseEntity multiple(@RequestParam Long... orderIds) {
		Assert.notNull(orderIds, "MULTIPLE_ORDER_IDS_IS_NULL");
		
		try {
			if (orderIds.length == 0) {
				return ResponseEntity.error("订单编号集合长度为0");
			}
			List<Order> data = orderService.get(orderIds);
			return ResponseEntity.success("获取订单信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取所有订单信息
	 * @return List<Order> 获取到的所有订单信息集合
	 */
	@GetMapping("list")
	public ResponseEntity list() {
		try {
			List<Order> data = orderService.get();
			return ResponseEntity.success("获取所有订单信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新订单状态
	 * @param orderId 订单编号
	 * @param status 订单状态
	 * @return boolean 是否更新成功
	 */
	@PutMapping("updatingStatus")
	public ResponseEntity udpatingStatus(@RequestParam Long orderId, @RequestParam Integer status) {
		Assert.notNull(orderId, "UPDATING_STATUS_ORDER_ID_IS_NULL");
		Assert.notNull(status, "UPDATING_STATUS_STATUS_IS_NULL");
		
		try {
			if (status == null || !Order.Status.exists(status.intValue())) {
				return ResponseEntity.error("订单状态格式错误");
			}
			if (orderId == 0) {
				return ResponseEntity.error("订单编号格式错误");
			}
			boolean data = orderService.updateStatus(orderId, Order.Status.getInstance(status));
			return ResponseEntity.success("更新订单状态成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新特定订单信息
	 * @param order 更新前的订单信息
	 * @return Order 更新后的订单信息
	 */
	@PutMapping("updating")
	public ResponseEntity updating(@RequestBody Order order) {
		Assert.notNull(order, "UPDATE_ORDER_IS_NULL");
		
		try {
			if (!(order.getUserId() != null && order.getUserId() > 0)) {
				return ResponseEntity.error("用户编号格式错误");
			}
			if (order.getAmount().doubleValue() < 0) {
				return ResponseEntity.error("订单金额格式错误");
			}
			if (StringUtils.isNotBlank(order.getAgent())) {
				if (!validateSize(order.getAgent(), 100)) {
					return ResponseEntity.error("代理商格式错误");
				}
			}
			if (order.getAmountForAgent().doubleValue() < 0) {
				return ResponseEntity.error("代理商结算金额格式错误");
			}
			if (StringUtils.isNotBlank(order.getOperator())) {
				if (!validateSize(order.getOperator(), 50)) {
					return ResponseEntity.error("运维人员格式错误");
				}
			}
			if (StringUtils.isBlank(order.getServiceIds())) {
				return ResponseEntity.error("具体服务格式错误");
			}
			if (StringUtils.isNotBlank(order.getDescription())) {
				if (!validateSize(order.getDescription(), 100)) {
					return ResponseEntity.error("备注格式错误");
				}
			}
			if (order.getServiceBeginTime() == null) {
				return ResponseEntity.error("服务开始时间格式错误");
			}
			if (order.getServiceEndTime() == null) {
				return ResponseEntity.error("服务结束时间格式错误");
			}
			if (order.getStatus() == null || !Order.Status.exists(order.getStatus().intValue())) {
				return ResponseEntity.error("订单状态格式错误");
			}
			Long orderId = order.getId();
			if (orderId == null || orderId == 0) {
				return ResponseEntity.error("订单编号不存在");
			}
			Order originalOrder = orderService.get(orderId);
			if (originalOrder == null) {
				return ResponseEntity.error("订单不存在");
			}
			Order data = orderService.update(order, originalOrder);
			return ResponseEntity.success("更新订单成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 删除单个或多个订单信息
	 * @param orderIds 删除单个订单或多个以订单编号为数组的订单信息
	 * @return long 有多少条订单信息被删除了
	 */
	@DeleteMapping("deletion")
	public ResponseEntity deletion(@RequestParam Long... orderIds) {
		Assert.notNull(orderIds, "DELETION_ORDER_IDS_IS_NULL");
		
		try {
			if (orderIds.length == 0) {
				return ResponseEntity.error("订单编号数组长度为0");
			}
			long data = orderService.delete(orderIds);
			return ResponseEntity.success("删除订单信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页订单数据
	 * @param map ->
	 * 				conditions 每个key对应属性，每个value对应搜索内容
	 * 				pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<Order> 查询到的分页数据
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam Map<String, Object> map) {
		Assert.notNull(map, "PAGE_MAP_IS_NULL");
		
		try {
			Page<Order> data =  orderService.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), orderService.search(JSON.parseObject(map.get("conditions").toString())));
			return ResponseEntity.success("获取分页订单信息成功", data);
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
