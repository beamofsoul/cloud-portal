package com.moraydata.general.primary.controller.openapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

@RequestMapping("/open_no_authentication")
@RestController
public class OpenNoAuthenticationController {

	@Autowired
	private UserService userService;
	
	/**
	 * 用户注册
	 * @param user 用户的注册信息
	 * @return 注册后的用户信息
	 */
	@PostMapping("/registration")
	public ResponseEntity registration(@RequestBody User user) {
		Assert.notNull(user, "REGISTRATION_USER_IS_NULL");
		
		if (!OpenUserController.validatePassword(user.getUsername())) {
			return ResponseEntity.error("用户名格式有误");
		}
		if (!userService.isUsernameUnique(user.getUsername(), null)) {
			return ResponseEntity.error("用户名已被使用");
		}
		if (!OpenUserController.validatePassword(user.getPassword())) {
			return ResponseEntity.error("密码格式有误");
		}
		if (!OpenUserController.validatePhone(user.getPhone())) {
			return ResponseEntity.error("手机号码格式有误");
		}
		User data = userService.create(user);
		if (data == null) {
			return ResponseEntity.UNKNOWN_ERROR;
		} else {
			return ResponseEntity.success("用户注册成功", data);
		}
	}
}
