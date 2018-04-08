package com.moraydata.general.primary.controller.openapi;

import static com.moraydata.general.management.util.RegexUtils.match;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.InvitationCode;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

import io.swagger.annotations.ApiOperation;

@RequestMapping("/open/user")
@RestController
public class OpenUserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户信息
	 */
	@ApiOperation(value = "用户登录", notes = "根据用户名、密码获取用户信息")
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
	 * 修改用户密码
	 * @param userId 用户编号
	 * @param oldPassword 未加密的原始密码
	 * @param newPassword 未加密的新密码
	 * @return boolean 是否成功修改了密码
	 */
	@PutMapping("changingPassword")
	public ResponseEntity changingPassword(@RequestParam Long userId, @RequestParam String oldPassword, @RequestParam String newPassword) {
		Assert.notNull(userId, "CHANGING_PASSWORD_USER_ID_IS_NULL");
		Assert.notNull(oldPassword, "CHANGING_PASSWORD_OLD_PASSWORD_IS_NULL");
		Assert.notNull(newPassword, "CHANGING_PASSWORD_NEW_PASSWORD_IS_NULL");
		
		try {
			boolean validated = validatePassword(newPassword);
			if (!validated) {
				return ResponseEntity.error("新密码格式有误");
			}
			validated = validatePassword(oldPassword);
			if (!validated) {
				return ResponseEntity.error("原始密码格式有误");
			}
			User target = userService.get(userId);
			if (target == null) {
				return ResponseEntity.error("用户不存在");
			} else if (!userService.matchPassword(oldPassword, target.getPassword())) {
				return ResponseEntity.error("原始密码错误");
			} else {
				boolean data = userService.updatePassword(userId, newPassword);
				if (data) {
					return ResponseEntity.success("修改密码成功", data);
				} else {
					return ResponseEntity.UNKNOWN_ERROR;
				}
			}
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
			boolean validated = validatePassword(newPassword);
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
	
	/**
	 * 更换手机 - 步骤1：输入新手机号码，用户新手机收到找回密码短信验证码，该接口返回找回密码验证码redis中对应的key
	 * @param phone 用户新手机号码
	 * @return key 下个步骤传回后台用以从redis中获取对应的验证码
	 */
	@GetMapping("keyOfChangingPhone")
	public ResponseEntity keyOfChangingPhone(@RequestParam String username, @RequestParam String phone, @RequestParam Long currentClientMilliseconds) {
		Assert.notNull(username, "KEY_OF_CHANGING_PHONE_USERNAME_IS_NULL");
		Assert.notNull(phone, "KEY_OF_CHANGING_PHONE_PHONE_IS_NULL");
		Assert.notNull(currentClientMilliseconds, "KEY_OF_CHANGING_PHONE_CURRENT_CLIENT_MILLISECONDS_IS_NULL");
		
		try {
			boolean validated = validatePhone(phone);
			if (!validated) {
				return ResponseEntity.error("手机号码格式无效");
			}
			boolean exists = userService.exists(username, phone);
			if (!exists) {
				return ResponseEntity.error("未能通过用户名和手机找到任何用户信息");
			} else {
				String data = userService.sendMessageCode4ChangingPhone(username, phone, currentClientMilliseconds);
				return ResponseEntity.success("更换手机验证码已经发送至用户手机", data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更换手机 - 步骤2：输入key、验证码code和新手机号码
	 * @param key 找回密码验证码redis中对应的key
	 * @param code 找回密码验证码
	 * @param phone 新的手机号码
	 * @return boolean 密码是否修改成功
	 */
	@PutMapping("changingPhone")
	public ResponseEntity changingPhone(@RequestParam String key, @RequestParam String code, @RequestParam String phone) {
		Assert.notNull(key, "CHANGING_PHONE_KEY_IS_NULL");
		Assert.notNull(code, "CHANGING_PHONE_CODE_IS_NULL");
		Assert.notNull(phone, "CHANGING_PHONE_PHONE_IS_NULL");
		
		try {
			boolean validated = validatePhone(phone);
			if (!validated) {
				return ResponseEntity.error("手机号码格式无效");
			}
			boolean matched = userService.matchPasswordCode(key, code);
			if (matched) {
				boolean updated = userService.updatePhone(key, phone);
				if (updated) {
					return ResponseEntity.success("更换手机成功", updated);
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
	
	/**
	 * 验证用户名是否唯一
	 * @param username 新的用户名
	 * @param userId 在修改用户名时可能用到的用户ID
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
	 * 验证昵称是否唯一
	 * @param nickname 新的用户昵称
	 * @param userId 在修改用户名时可能用到的用户ID
	 * @return boolean 用户名是否唯一
	 */
	@GetMapping("uniqueNickname")
	public ResponseEntity uniqueNickname(@RequestParam String nickname, @RequestParam(required = false) Long userId) {
		Assert.notNull(nickname, "UNIQUE_NICKNAME_NICKNAME_IS_NULL");
		
		try {
			boolean data = userService.isNicknameUnique(nickname, userId);
			return ResponseEntity.success("验证用户昵称唯一成功", data);
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
	@PutMapping("update")
	public ResponseEntity update(@RequestBody User user) {
		Assert.notNull(user, "UPDATE_USER_IS_NULL");
		
		try {
			Long userId = user.getId();
			if (userId == null || userId == 0) {
				return ResponseEntity.error("更新用户时用户编号不存在");
			}
			User originalUser = userService.get(userId);
			if (originalUser == null) {
				return ResponseEntity.error("用户不存在");
			}
			User data = userService.update(user, originalUser);
			return ResponseEntity.success("更新用户成功", data);
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
				return ResponseEntity.error("用户编号集合长度为0");
			}
			long data = userService.delete(userIds);
			return ResponseEntity.success("删除用户信息集合成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 绑定上级用户
	 * @param invitationCode 绑定上级用户所需的激活码
	 * @param currentUserId 绑定上级用户的用户编号
	 * @return long 有多少条用户信息绑定上级用户
	 */
	@PutMapping("bindingParent")
	public ResponseEntity bindingParent(@RequestParam String invitationCode, @RequestParam Long currentUserId) {
		Assert.notNull(invitationCode, "BINDING_PARENT_INVITATION_CODE_IS_NULL");
		Assert.notNull(currentUserId, "BINDING_PARENT_CURRENT_USER_ID_IS_NULL");
		
		try {
			if (currentUserId.longValue() == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			InvitationCode code = userService.getBindParentInvitationCode(invitationCode);
			if (code == null) {
				return ResponseEntity.error("绑定上级用户验证码无效");
			} else if (!code.getAvailable()) {
				return ResponseEntity.error("当前绑定上级用户验证码不可用");
			}
			
			long data = userService.bindParentByInvitationCode(code, currentUserId);
			return ResponseEntity.success("绑定上级用户成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新用户名
	 * @param userId 更新的用户编号
	 * @param username 更新后的用户名
	 * @return boolean 是否更新成功
	 */
	@PutMapping("changingUsername")
	public ResponseEntity changingUsername(@RequestParam Long userId, @RequestParam String username) {
		Assert.notNull(userId, "CHANGING_USERNAME_USER_ID_IS_NULL");
		Assert.notNull(username, "CHANGING_USERNAME_USERNAME_IS_NULL");
		
		try {
			if (userId.longValue() == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			boolean matched = validateUsername(username);
			if (!matched) {
				return ResponseEntity.error("更新用户名所用用户名格式错误");
			}
			boolean isUnique = userService.isUsernameUnique(username, userId);
			if (!isUnique) {
				return ResponseEntity.error("更新用户名所用用户名已被使用");
			}
			boolean data = userService.updateUsername(userId, username);
			return ResponseEntity.success("更新用户名成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取符合查询后的分页用户数据
	 * @param conditions 每个key对应属性，每个value对应搜索内容
	 * @param pageable key可以有page、size、sort和direction，具体value针对每个属性值
	 * @return Page<User>
	 */
	@GetMapping("/page")
	public ResponseEntity page(@RequestParam JSONObject conditions, @RequestParam JSONObject pageable) {
		try {
			Page<User> data =  userService.get(PageUtils.parsePageable(pageable), userService.search(conditions));
			return ResponseEntity.success("获取分页用户信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	static boolean validateUsername(String username) {
		String regex = "^[\\u4E00-\\u9FA5a-zA-Z][\\u4E00-\\u9FA5a-zA-Z0-9]{5,15}$";
		return match(username, regex);
	}
	
	static boolean validatePhone(String phone) {
		String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
		return match(phone, regex);
	}
	
	static boolean validatePassword(String password) {
		String regex = "^[a-zA-Z][a-zA-Z0-9]{5,15}$";
		return match(password, regex);
	}
}
