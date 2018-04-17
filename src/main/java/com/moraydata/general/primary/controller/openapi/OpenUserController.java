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
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

@RequestMapping("/open/user")
@RestController
public class OpenUserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return User 用户信息
	 */
	@GetMapping("login")
	public ResponseEntity login(@RequestParam String username, @RequestParam String password) {
		Assert.notNull(username, "LOGIN_USERNAME_IS_NULL");
		Assert.notNull(password, "LOGIN_PASSWORD_IS_NULL");
		
		try {
			User data = userService.get(username);
			if (data == null) {
				return ResponseEntity.error("用户不存在");
			} else {
				if (!userService.matchPassword(password, data.getPassword())) {
					return ResponseEntity.error("用户名和密码不匹配");
				} else {
					return ResponseEntity.success("登录成功", data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 验证用户名是否唯一
	 * @param username 新的用户名
	 * @param userId 在修改用户名时可能用到的用户ID，排除此userId指向的用户
	 * @return boolean 用户名是否唯一
	 */
	@GetMapping("uniqueUsername")
	public ResponseEntity uniqueUsername(@RequestParam String username, @RequestParam(required = false) Long userId) {
		Assert.notNull(username, "UNIQUE_USERNAME_USERNAME_IS_NULL");
		
		try {
			boolean matched = validateUsername(username);
			if (!matched) {
				return ResponseEntity.error("用户名格式错误");
			}
			boolean data = userService.isUsernameUnique(username, userId);
			return ResponseEntity.success("验证用户名唯一成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 验证用户手机号码是否唯一
	 * @param phone 新的手机号码
	 * @param userId 在修改用户手机号码时可能用到的用户ID，排除此userId指向的用户
	 * @return boolean 手机号码是否唯一
	 */
	@GetMapping("uniquePhone")
	public ResponseEntity uniquePhone(@RequestParam String phone, @RequestParam(required = false) Long userId) {
		Assert.notNull(phone, "UNIQUE_PHONE_PHONE_IS_NULL");
		
		try {
			boolean matched = validatePhone(phone);
			if (!matched) {
				return ResponseEntity.error("手机号码格式错误");
			}
			boolean data = userService.isPhoneUnique(phone, userId);
			return ResponseEntity.success("验证手机号码唯一成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定用户信息
	 * @param userId
	 * @return User 获取到的用户信息
	 */
	@GetMapping("single")
	public ResponseEntity single(@RequestParam Long userId) {
		Assert.notNull(userId, "SINGLE_USER_ID_IS_NULL");
		
		try {
			if (userId == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			User data =  userService.get(userId);
			return ResponseEntity.success("获取用户信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取特定单个或多个用户信息
	 * @param userIds 单个userId或多个userId组成的数组
	 * @return List<User> 获取到的用户信息集合
	 */
	@GetMapping("multiple")
	public ResponseEntity multiple(@RequestParam Long... userIds) {
		Assert.notNull(userIds, "MULTIPLE_USER_IDS_IS_NULL");
		
		try {
			if (userIds.length == 0) {
				return ResponseEntity.error("用户编号集合长度为0");
			}
			List<User> data = userService.get(userIds);
			return ResponseEntity.success("获取用户信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新特定用户信息
	 * @param user 更新前的用户信息
	 * @return User 更新后的用户信息
	 */
	@PutMapping("updating")
	public ResponseEntity update(@RequestBody User user) {
		Assert.notNull(user, "UPDATING_USER_IS_NULL");
		
		try {
			Long userId = user.getId();
			if (userId == null || userId == 0) {
				return ResponseEntity.error("更新用户时用户编号不存在");
			}
			User originalUser = userService.get(userId);
			if (originalUser == null) {
				return ResponseEntity.error("用户不存在");
			}
			if (!OpenUserController.validateUsername(user.getUsername())) {
				return ResponseEntity.error("用户名格式有误");
			}
			if (!userService.isUsernameUnique(user.getUsername(), userId)) {
				return ResponseEntity.error("用户名已被使用");
			}
			if (user.getPassword() != null) { // 后台修改时密码为空，意味着未修改密码。否则会被认为密码已被修改，新密码必须符合验证规则。
				if (!OpenUserController.validatePassword(user.getPassword())) {
					return ResponseEntity.error("密码格式有误");
				}
			}
			if (StringUtils.isNotBlank(user.getPhone())) {
				if (!OpenUserController.validatePhone(user.getPhone())) {
					return ResponseEntity.error("手机号码格式有误");
				}
				if (!userService.isPhoneUnique(user.getPhone(), userId)) {
					return ResponseEntity.error("手机号码已被使用");
				}
			}
			if (StringUtils.isNotBlank(user.getNickname()) && !validateNickname(user.getNickname())) {
				return ResponseEntity.error("用户姓名格式错误");
			}
			if (user.getCompany() != null && !validateSize(user.getCompany(), 20)) {
				return ResponseEntity.error("所属单位格式错误");
			}
			if (user.getCompanyTitle() != null && !validateSize(user.getCompanyTitle(), 10)) {
				return ResponseEntity.error("职务格式错误");
			}
			if (user.getCompanyLocation() != null && !validateSize(user.getCompanyLocation(), 20)) {
				return ResponseEntity.error("企业所在地格式错误");
			}
			if (user.getCompanyType() != null && !validateSize(user.getCompanyType(), 10)) {
				return ResponseEntity.error("企业类型格式错误");
			}
			if (user.getCompanyPhone() != null && !validateSize(user.getCompanyPhone(), 20)) {
				return ResponseEntity.error("企业联系电话格式错误");
			}
			if (user.getCompanyFax() != null && !validateSize(user.getCompanyFax(), 20)) {
				return ResponseEntity.error("企业传真格式错误");
			}
			if (user.getDescription() != null && !validateSize(user.getDescription(), 20)) {
				return ResponseEntity.error("描述格式错误");
			}
			User data = userService.update(user, originalUser);
			return ResponseEntity.success("更新用户成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 增加用户
	 * @param user 新用户的信息
	 * @return User 增加后的用户信息
	 */
	@PostMapping("/addition")
	public ResponseEntity addition(@RequestBody User user) {
		Assert.notNull(user, "ADDITION_USER_IS_NULL");
		
		try {
			if (!OpenUserController.validateUsername(user.getUsername())) {
				return ResponseEntity.error("用户名格式有误");
			}
			if (!userService.isUsernameUnique(user.getUsername(), null)) {
				return ResponseEntity.error("用户名已被使用");
			}
			if (!OpenUserController.validatePassword(user.getPassword())) {
				return ResponseEntity.error("密码格式有误");
			}
			if (StringUtils.isNotBlank(user.getPhone())) {
				if (!OpenUserController.validatePhone(user.getPhone())) {
					return ResponseEntity.error("手机号码格式有误");
				}
				if (!userService.isPhoneUnique(user.getPhone(), null)) {
					return ResponseEntity.error("手机号码已被使用");
				}
			}
			if (StringUtils.isNotBlank(user.getNickname()) && !validateNickname(user.getNickname())) {
				return ResponseEntity.error("用户姓名格式错误");
			}
			if (user.getCompany() != null && !validateSize(user.getCompany(), 20)) {
				return ResponseEntity.error("所属单位格式错误");
			}
			if (user.getCompanyTitle() != null && !validateSize(user.getCompanyTitle(), 10)) {
				return ResponseEntity.error("职务格式错误");
			}
			if (user.getCompanyLocation() != null && !validateSize(user.getCompanyLocation(), 20)) {
				return ResponseEntity.error("企业所在地格式错误");
			}
			if (user.getCompanyType() != null && !validateSize(user.getCompanyType(), 10)) {
				return ResponseEntity.error("企业类型格式错误");
			}
			if (user.getCompanyPhone() != null && !validateSize(user.getCompanyPhone(), 20)) {
				return ResponseEntity.error("企业联系电话格式错误");
			}
			if (user.getCompanyFax() != null && !validateSize(user.getCompanyFax(), 20)) {
				return ResponseEntity.error("企业传真格式错误");
			}
			if (user.getDescription() != null && !validateSize(user.getDescription(), 20)) {
				return ResponseEntity.error("描述格式错误");
			}
			User data = userService.create(user);
			return ResponseEntity.success("用户注册成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 删除单个或多个用户信息
	 * @param userIds 删除单个用户或多个以用户编号为数组的用户信息
	 * @return long 有多少条用户信息被删除了
	 */
	@DeleteMapping("deletion")
	public ResponseEntity deletion(@RequestParam Long... userIds) {
		Assert.notNull(userIds, "DELETION_USER_IDS_IS_NULL");
		
		try {
			if (userIds.length == 0) {
				return ResponseEntity.error("用户编号数组长度为0");
			}
			long data = userService.delete(userIds);
			return ResponseEntity.success("删除用户信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页用户数据
	 * @param map ->
	 * 				conditions 每个key对应属性，每个value对应搜索内容
	 * 				pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<User>
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam Map<String, Object> map) {
		try {
			Page<User> data =  userService.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), userService.search(JSON.parseObject(map.get("conditions").toString())));
			return ResponseEntity.success("获取分页用户信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	static boolean validateUsername(String username) {
		if (StringUtils.isBlank(username)) {
			return false;
		}
		String regex = "^[\\u4E00-\\u9FA5a-zA-Z][\\u4E00-\\u9FA5a-zA-Z0-9]{5,15}$";
		return match(username, regex);
	}
	
	static boolean validatePhone(String phone) {
		String regex = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
		return match(phone, regex);
	}
	
	static boolean validatePassword(String password) {
		if (StringUtils.isBlank(password)) {
			return false;
		}
		String regex = "^[a-zA-Z][a-zA-Z0-9]{5,15}$";
		return match(password, regex);
	}
	
	static boolean validateNickname(String nickname) {
		String regex = "^[\\u4E00-\\u9FA5]{0,10}$";
		return match(nickname, regex);
	}
	
	static boolean validateSize(String string, int size) {
		if (StringUtils.isBlank(string)) {
			return true;
		}
		String regex = String.format("^.{1,%s}$", size);
		return match(string, regex);
	}
}
