package com.moraydata.general.primary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/level1UserIds/{userId}")
	public ResponseEntity level1UserBasicInformation(@PathVariable Long userId) {
		Assert.notNull(userId, "LEVEL_1_USER_BASIC_INFORMATION");
		try {
			List<Long> data = userService.getLevelUserIds(userId, User.Level.FIRST);
			return ResponseEntity.success("获取当前用户父用户下所有1级别用户编号成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
}
