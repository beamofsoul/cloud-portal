package com.moraydata.general.primary.controller.openapi;

import static com.moraydata.general.management.util.RegexUtils.match;

import java.util.List;

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
import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.PageUtils;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.InvitationCode;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.RoleService;
import com.moraydata.general.primary.service.UserService;

@RequestMapping("/open/user")
@RestController
public class OpenUserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	/**
	 * 主用户创建子用户
	 * @param user 子用户的注册信息
	 * @return 注册后的子用户信息
	 */
	@PostMapping("/addingSlave")
	public ResponseEntity addingSlave(@RequestBody User user) {
		Assert.notNull(user, "ADDING_SLAVE_USER_IS_NULL");
		
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
		
			User data = userService.create(user, roleService.get(Constants.ROLE.SLAVE_ROLE_NAME));
			return ResponseEntity.success("用户注册成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 通过主用户userId获取子用户信息列表
	 * @param userId 主用户userId
	 * @return List<User> 获取到的子用户信息列表
	 */
	@GetMapping("slaves")
	public ResponseEntity slaves(@RequestParam Long userId) {
		Assert.notNull(userId, "SLAVES_USER_ID_IS_NULL");
		
		try {
			List<User> data = userService.getByParentId(userId);
			return ResponseEntity.success("获取子用户信息列表成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户信息
	 */
	@GetMapping("login")
	public ResponseEntity login(@RequestParam String username, @RequestParam String password) {
		Assert.notNull(username, "LOGIN_USERNAME_IS_NULL");
		Assert.notNull(password, "LOGIN_PASSWORD_IS_NULL");
		
		try {
			// 判断是否通过微信扫码登录，提供的username实际为openId，两者区分暂时根据字符串长度界定
			if (username.length() > Constants.WECHAT.SCAN_LOGIN_OPEN_ID_MIN_LENGTH) {
				User data = userService.getByOpenId(username);
				if (data != null) {
					// 微信扫码登录，不需要检查用户真实密码，但是需要检查借由参数password传入的场景值sceneId
					if (password.equals(Constants.WECHAT.SCAN_LOGIN_SCENE_ID)) {
						return ResponseEntity.success("登录成功", data);
					}
					return ResponseEntity.error("微信扫码登录场景值错误");
				}
				return ResponseEntity.error("用户不存在");
			} else {
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
	 * 更换手机 - 步骤1：输入新手机号码，用户新手机收到更新手机短信验证码，该接口返回更新手机验证码redis中对应的key
	 * @param username 用户名
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
			boolean exists = userService.existsByUsername(username);
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
			if (!userService.isPhoneUnique(phone, null)) {
				return ResponseEntity.error("手机号码已被使用");
			}
			boolean matched = userService.matchPhoneCode(key, code);
			if (matched) {
				boolean updated = userService.updatePhone(key, phone);
				if (updated) {
					return ResponseEntity.success("更换手机号码成功", updated);
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
	 * 更新子账号信息
	 * @param userId 子账号Id
	 * @param password 子账号的新密码
	 * @param phone 子账号的新手机号码
	 * @param description 子账号的新描述信息
	 * @return boolean 是否更新成功
	 */
	@PutMapping("updateSlave")
	public ResponseEntity updateSlave(@RequestParam Long userId, @RequestParam(required = false) String password, @RequestParam(required = false) String phone, @RequestParam(required = false) String description) {
		Assert.notNull(userId, "UPDATE_SLAVE_USER_IS_NULL");
		
		try {
			if (password == phone && phone == description && password == null) {
				return ResponseEntity.error("无效的更新参数");
			}
			if (StringUtils.isNotBlank(password)) {
				if (!validatePassword(password)) {
					return ResponseEntity.error("密码格式错误");
				}
			}
			if (StringUtils.isNotBlank(phone)) {
				if (!validatePhone(phone)) {
					return ResponseEntity.error("手机号码格式错误");
				}
				if (!userService.isPhoneUnique(phone, userId)) {
					return ResponseEntity.error("手机号码已被使用");
				}
			}
			if (!validateSize(description, 20)) {
				return ResponseEntity.error("描述格式错误");
			}
			boolean data = userService.update(userId, password, phone, description);
			return ResponseEntity.success("更新子用户信息成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新特定用户信息(包括姓名和所有和公司有关的信息)
	 * @param user 更新前的用户信息
	 * @return boolean 是否成功更新
	 */
	@PutMapping("updateProfile")
	public ResponseEntity updateProfile(@RequestBody User user) {
		Assert.notNull(user, "UPDATE_PROFILE_USER_IS_NULL");
		
		try {
			Long userId = user.getId();
			if (userId == null || userId == 0) {
				return ResponseEntity.error("更新用户简况时用户编号不存在");
			}
			if (!userService.exists(userId)) {
				return ResponseEntity.error("用户不存在");
			}
			if (!(StringUtils.isNotBlank(user.getNickname()) && validateNickname(user.getNickname()))) {
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
			
			boolean data = userService.updateProfile(user);
			return ResponseEntity.success("更新用户简况成功", data);
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
	 * 删除单个或多个子用户信息
	 * @param userId 主账号用户Id
	 * @param userIds 单个或子账号用户Id列表
	 * @return long 有多少条用户信息被删除了
	 */
	@DeleteMapping("deletionSlaves")
	public ResponseEntity deletionSlaves(@RequestParam Long userId, @RequestParam Long... userIds) {
		Assert.notNull(userId, "DELETION_SLAVES_USER_ID_IS_NULL");
		Assert.notNull(userIds, "DELETION_SLAVES_USER_IDS_IS_NULL");
		
		try {
			if (userIds.length == 0) {
				return ResponseEntity.error("子账号编号数组长度为0");
			}
			if (!userService.matchRelationship(userId, userIds)) {
				return ResponseEntity.error("输入的主账号和子账号之间并无父子关系");
			}
			long data = userService.delete(userIds);
			return ResponseEntity.success("删除子账号用户信息成功", data);
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
	 * 修改用户级别
	 * @param userId 修改所用的用户编号
	 * @param level 修改后的用户级别
	 * @return boolean 是否修改成功
	 */
	@PutMapping("changingLevel")
	public ResponseEntity changingLevel(@RequestParam Long userId, @RequestParam Integer level) {
		Assert.notNull(userId, "CHANGING_LEVEL_USER_ID_IS_NULL");
		Assert.notNull(level, "CHANGING_LEVEL_LEVEL_IS_NULL");
		
		try {
			if (userId.longValue() == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			if (!User.Level.exists(level.intValue())) {
				return ResponseEntity.error("用户级别格式错误");
			}
			boolean data = userService.updateLevel(userId, User.Level.getInstance(level.intValue()));
			return ResponseEntity.success("更新用户级别成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 修改用户是否接受通知消息状态
	 * @param userId 修改所用的用户编号
	 * @param notified 修改后的用户是否接受通知消息状态
	 * @return boolean 是否修改成功
	 */
	@PutMapping("changingNotified")
	public ResponseEntity changingNotified(@RequestParam Long userId, @RequestParam Boolean notified) {
		Assert.notNull(userId, "CHANGING_NOTIFIED_USER_ID_IS_NULL");
		Assert.notNull(notified, "CHANGING_NOTIFIED_NOTIFIED_IS_NULL");
		
		try {
			if (userId.longValue() == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			boolean data = userService.updateNotified(userId, notified);
			return ResponseEntity.success("更新用户接受消息状态成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取全部舆情接受状态
	 * @return String 所有舆情接受状态的Map的JSON字符串
	 */
	@GetMapping("sentiments")
	public ResponseEntity sentiments() {
		try {
			String data = JSON.toJSONString(User.NotifiedSentiment.CODE_VALUE_MAP);
			return ResponseEntity.success("获取全部舆情接受状态成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 修改用户对预警舆情的接受状态
	 * @param userId 修改所用的用户编号
	 * @param sentiment 修改后用户对预警舆情的接受状态
	 * @return boolean 是否修改成功
	 */
	@PutMapping("changingNotifiedWarningPublicSentiment")
	public ResponseEntity changingNotifiedWarningPublicSentiment(@RequestParam Long userId, @RequestParam String sentiment) {
		Assert.notNull(userId, "CHANGING_NOTIFIED_WARNING_PUBLIC_SENTIMENT_USER_ID_IS_NULL");
		Assert.notNull(sentiment, "CHANGING_NOTIFIED_WARNING_PUBLIC_SENTIMENT_SENTIMENT_IS_NULL");
		
		try {
			if (userId.longValue() == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			if (!User.NotifiedSentiment.exists(sentiment)) {
				return ResponseEntity.error("预警舆情接受状态格式错误");
			}
			boolean data = userService.updateNotifiedWarningPublicSentiment(userId, User.NotifiedSentiment.getInstance(sentiment));
			return ResponseEntity.success("预警舆情接受状态修改成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 修改用户对热点舆情的接受状态
	 * @param userId 修改所用的用户编号
	 * @param sentiment 修改后用户对热点舆情的接受状态
	 * @return boolean 是否修改成功
	 */
	@PutMapping("changingNotifiedHotPublicSentiment")
	public ResponseEntity changingNotifiedHotPublicSentiment(@RequestParam Long userId, @RequestParam String sentiment) {
		Assert.notNull(userId, "CHANGING_NOTIFIED_HOT_PUBLIC_SENTIMENT_USER_ID_IS_NULL");
		Assert.notNull(sentiment, "CHANGING_NOTIFIED_HOT_PUBLIC_SENTIMENT_SENTIMENT_IS_NULL");
		
		try {
			if (userId.longValue() == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			if (!User.NotifiedSentiment.exists(sentiment)) {
				return ResponseEntity.error("热点舆情接受状态格式错误");
			}
			boolean data = userService.updateNotifiedHotPublicSentiment(userId, User.NotifiedSentiment.getInstance(sentiment));
			return ResponseEntity.success("热点舆情接受状态修改成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 修改用户对负面舆情的接受状态
	 * @param userId 修改所用的用户编号
	 * @param sentiment 修改后用户对负面舆情的接受状态
	 * @return boolean 是否修改成功
	 */
	@PutMapping("changingNotifiedNegativePublicSentiment")
	public ResponseEntity changingNotifiedNegativePublicSentiment(@RequestParam Long userId, @RequestParam String sentiment) {
		Assert.notNull(userId, "CHANGING_NOTIFIED_NEGATIVE_PUBLIC_SENTIMENT_USER_ID_IS_NULL");
		Assert.notNull(sentiment, "CHANGING_NOTIFIED_NEGATIVE_PUBLIC_SENTIMENT_SENTIMENT_IS_NULL");
		
		try {
			if (userId.longValue() == 0L) {
				return ResponseEntity.error("用户编号有误");
			}
			if (!User.NotifiedSentiment.exists(sentiment)) {
				return ResponseEntity.error("负面舆情接受状态格式错误");
			}
			boolean data = userService.updateNotifiedNegativePublicSentiment(userId, User.NotifiedSentiment.getInstance(sentiment));
			return ResponseEntity.success("负面舆情接受状态修改成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新用户服务订单细则编号
	 * @param orderItemIds 新的用户服务订单细则编号
	 * @param userId 目标用户
	 * @return boolean 是否更新成功
	 */
	@PutMapping("changingOrderItemIds")
	public ResponseEntity changingOrderItemIds(@RequestParam String orderItemIds, @RequestParam Long userId) {
		Assert.notNull(orderItemIds, "CHANGING_ORDER_ITEM_IDS_ORDER_ITEM_IDS_IS_NULL");
		Assert.notNull(userId, "CHANGING_ORDER_ITEM_IDS_USER_ID_IS_NULL");
		
		try {
			boolean data = userService.updateOrderItemIds(userId, orderItemIds);
			return ResponseEntity.success("更新用户服务订单细则编号成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 更新子账号服务订单细则编号
	 * @param orderItemIds 新的用户服务订单细则编号
	 * @param masterUserId 目标用户的主账号Id
	 * @param userId 目标用户
	 * @return boolean 是否更新成功
	 */
	@PutMapping("updateSlaveOrderItems")
	public ResponseEntity changingOrderItemIds(@RequestParam Long masterUserId, @RequestParam String orderItemIds, @RequestParam Long userId) {
		Assert.notNull(orderItemIds, "CHANGING_ORDER_ITEM_IDS_ORDER_ITEM_IDS_IS_NULL");
		Assert.notNull(userId, "CHANGING_ORDER_ITEM_IDS_USER_ID_IS_NULL");
		Assert.notNull(masterUserId, "CHANGING_ORDER_ITEM_IDS_MASTER_USER_ID_IS_NULL");
		
		try {
			if (!userService.matchRelationship(masterUserId, userId)) {
				return ResponseEntity.error("输入的主账号和子账号之间并无父子关系");
			}
			boolean data = userService.updateOrderItemIds(userId, orderItemIds);
			return ResponseEntity.success("更新子账号服务订单细则编号成功", data);
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
