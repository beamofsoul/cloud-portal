package com.moraydata.general.primary.controller.openapi;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.RoleService;
import com.moraydata.general.primary.service.UserService;

@RequestMapping("/open_no_authentication")
@RestController
public class OpenNoAuthenticationController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	/**
	 * 用户注册 - 步骤1：用户注册时填写手机号码后，点击获取验证码，用户手机收到验证码，该接口返回用户注册验证码redis中对应的key
	 * @param phone 注册用户的手机号码
	 * @param currentClientMilliseconds 客户端时间戳
	 * @return key 下个步骤传回后台用以从redis中获取对应的验证码
	 */
	@GetMapping("/keyOfRegistration")
	public ResponseEntity keyOfRegistration(@RequestParam String phone, @RequestParam Long currentClientMilliseconds) {
		Assert.notNull(phone, "KEY_OF_REGISTRATION_PHONE_IS_NULL");

		try {
			boolean validated = OpenUserController.validatePhone(phone);
			if (!validated) {
				return ResponseEntity.error("手机号码格式无效");
			}
			boolean exists = userService.exists(phone);
			if (exists) {
				return ResponseEntity.error("该手机号码已被使用");
			}
			String data = userService.sendMessageCode4Registration(phone, currentClientMilliseconds);
			return ResponseEntity.success("注册账号验证码已经发送至用户手机", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 用户注册 - 步骤2：验证后注册用户信息
	 * @param user 用户的注册信息
	 * @return 注册后的用户信息
	 */
	@PostMapping("/registration")
	public ResponseEntity registration(@RequestBody User user, HttpServletRequest request) {
		Assert.notNull(user, "REGISTRATION_USER_IS_NULL");
		
		try {
			if (!OpenUserController.validateSize(user.getCompany(), 20)) {
				return ResponseEntity.error("公司/单位名称格式错误");
			}
			if (!OpenUserController.validateUsername(user.getUsername())) {
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
			String key = request.getHeader("MESSAGE_KEY");
			String code = request.getHeader("MESSAGE_CODE");
			if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
				return ResponseEntity.error("手机验证码或对应的键格式错误");
			}
			boolean matched = userService.matchRegistrationCode(key, code);
			if (!matched) {
				return ResponseEntity.error("手机验证码错误或已过期");
			}
			User data = userService.create(user, roleService.get(Constants.ROLE.TRIAL_ROLE_NAME));
			return ResponseEntity.success("用户注册成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 找回密码 - 步骤1：输入用户名、手机号码和客户端时间戳，用户手机收到找回密码短信验证码，该接口返回找回密码验证码redis中对应的key
	 * @param username 用户名
	 * @param phone 用户手机号码
	 * @param currentClientMilliseconds 客户端时间戳
	 * @return key 下个步骤传回后台用以从redis中获取对应的验证码
	 */
	@GetMapping("keyOfRetakingPassword")
	public ResponseEntity keyOfRetakingPassword(@RequestParam String username, @RequestParam String phone, @RequestParam Long currentClientMilliseconds) {
		Assert.notNull(username, "KEY_OF_RETAKING_PASSWORD_USERNAME_IS_NULL");
		Assert.notNull(phone, "KEY_OF_RETAKING_PASSWORD_PHONE_IS_NULL");
		Assert.notNull(currentClientMilliseconds, "KEY_OF_RETAKING_PASSWORD_CURRENT_CLIENT_MILLISECONDS_IS_NULL");
		
		try {
			boolean exists = userService.exists(username, phone);
			if (!exists) {
				return ResponseEntity.error("未能通过用户名和手机找到任何用户信息");
			} else {
				String data = userService.sendMessageCode4RetakingPassword(username, phone, currentClientMilliseconds);
				return ResponseEntity.success("找回密码验证码已经发送至用户手机", data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 找回密码 - 步骤2：输入key、验证码code和新密码
	 * @param key 找回密码验证码redis中对应的key
	 * @param code 找回密码验证码
	 * @param newPassword 新的密码
	 * @return boolean 密码是否修改成功
	 */
	@PutMapping("retakingPassword")
	public ResponseEntity retakingPassword(@RequestParam String key, @RequestParam String code, @RequestParam String newPassword) {
		Assert.notNull(key, "RETAKING_PASSWORD_KEY_IS_NULL");
		Assert.notNull(code, "RETAKING_PASSWORD_CODE_IS_NULL");
		Assert.notNull(newPassword, "RETAKING_PASSWORD_NEW_PASSWORD_IS_NULL");
		
		try {
			boolean validated = OpenUserController.validatePassword(newPassword);
			if (!validated) {
				return ResponseEntity.error("密码格式有误");
			}
			boolean matched = userService.matchPasswordCode(key, code);
			if (matched) {
				boolean updated = userService.updatePassword(key, newPassword);
				if (updated) {
					return ResponseEntity.success("修改密码成功", updated);
				} else {
					return ResponseEntity.UNKNOWN_ERROR;
				}
			} else {
				return ResponseEntity.error("验证码错误或已经过期");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
}
