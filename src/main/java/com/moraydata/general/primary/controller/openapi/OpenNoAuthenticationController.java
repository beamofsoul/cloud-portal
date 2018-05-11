package com.moraydata.general.primary.controller.openapi;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

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

import com.alibaba.fastjson.JSON;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;
import com.moraydata.general.primary.entity.dto.UserMiniInformation;
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
	 * 验证密码是否正确，用户登录时区分密码错误，还是token过期 
	 */
	@GetMapping("checkingPassword")
	public ResponseEntity checkingPassword(@RequestParam String username, @RequestParam String password) {
		Assert.notNull(username, "CHECKING_PASSWORD_USERNAME_IS_NULL");
		Assert.notNull(password, "CHECKING_PASSWORD_PASSWORD_IS_NULL");
		
		try {
			boolean correct = userService.existsByUsernameAndPassword(username, password);
			return ResponseEntity.success("验证密码是否正确成功", correct);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
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
			if (!userService.isPhoneUnique(user.getPhone(), null)) {
				return ResponseEntity.error("手机号码已被使用");
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
	 * 找回密码 - 步骤1：输入手机号码(手机号码唯一)和客户端时间戳，用户手机收到找回密码短信验证码，该接口返回找回密码验证码redis中对应的key
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
	public ResponseEntity retakingPassword(@RequestParam String newPassword, HttpServletRequest request) {
		Assert.notNull(newPassword, "RETAKING_PASSWORD_NEW_PASSWORD_IS_NULL");
		
		try {
			boolean validated = OpenUserController.validatePassword(newPassword);
			if (!validated) {
				return ResponseEntity.error("密码格式有误");
			}
			
			String key = request.getHeader("MESSAGE_KEY");
			String code = request.getHeader("MESSAGE_CODE");
			if (StringUtils.isBlank(key) || StringUtils.isBlank(code)) {
				return ResponseEntity.error("手机验证码或对应的键格式错误");
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
	 * 获取加密的用户级别为1的用户基本信息，留给内部其他模块的api调用所用
	 * @return String Base64加密后的用户基本信息列表
	 */
	@GetMapping("encodedLevel1UserBasicInformation")
	public ResponseEntity encodedLevel1UserBasicInformation() throws Exception {
		try {
			List<UserBasicInformation> list = userService.getAllIdAndUsernameWhoHasOpenId();
			String encodedData = Base64.getEncoder().encodeToString(JSON.toJSONString(list).getBytes(StandardCharsets.UTF_8));
			return ResponseEntity.success("获取用户基本信息成功", encodedData);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 获取加密的所有用户基本信息，留给内部其他模块的api调用所用
	 * @return String Base64加密后的用户迷你信息列表
	 */
	@GetMapping("encodedUserMiniInformation")
	public ResponseEntity encodedUserBasicInformation() throws Exception {
		try {
			List<UserMiniInformation> list = userService.getAllIdAndUsername();
			String encodedData = Base64.getEncoder().encodeToString(JSON.toJSONString(list).getBytes(StandardCharsets.UTF_8));
			return ResponseEntity.success("获取用户迷你信息成功", encodedData);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 只通过openId在未授权的情况下获取对应用户的个人信息
	 * @param openId 目标用户的openId
	 * @return User 获取到的用户信息
	 */
	@GetMapping("insecureUserInformation")
	public ResponseEntity insecureUserInformation(@RequestParam String openId) throws Exception {
		Assert.notNull(openId, "INSECURE_USER_INFORMATION_OPEN_ID_IS_NULL");
		
		try {
			User data = userService.getByOpenId(openId);
			return ResponseEntity.success("用户信息获取成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 修改用户级别
	 * @param openId 目标用户的openId
	 * @param level 修改后的用户级别
	 * @return boolean 是否修改成功
	 */
	@PutMapping("insecureChangingLevel")
	public ResponseEntity insecureChangingLevel(@RequestParam String openId, @RequestParam Integer level) {
		Assert.notNull(openId, "CHANGING_LEVEL_OPEN_ID_IS_NULL");
		Assert.notNull(level, "CHANGING_LEVEL_LEVEL_IS_NULL");
		
		try {
			boolean data = userService.updateLevel(openId, User.Level.getInstance(level.intValue()));
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
	@PutMapping("insecureChangingNotified")
	public ResponseEntity insecureChangingNotified(@RequestParam String openId, @RequestParam Boolean notified) {
		Assert.notNull(openId, "INSECURE_CHANGING_NOTIFIED_USER_ID_IS_NULL");
		Assert.notNull(notified, "INSECURE_CHANGING_NOTIFIED_NOTIFIED_IS_NULL");
		
		try {
			boolean data = userService.updateNotified(openId, notified);
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
	@GetMapping("insecureSentiments")
	public ResponseEntity insecureSentiments() {
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
	@PutMapping("insecureChangingNotifiedWarningPublicSentiment")
	public ResponseEntity insecureChangingNotifiedWarningPublicSentiment(@RequestParam String openId, @RequestParam String sentiment) {
		Assert.notNull(openId, "INSECURE_CHANGING_NOTIFIED_WARNING_PUBLIC_SENTIMENT_USER_ID_IS_NULL");
		Assert.notNull(sentiment, "INSECURE_CHANGING_NOTIFIED_WARNING_PUBLIC_SENTIMENT_SENTIMENT_IS_NULL");
		
		try {
			boolean data = userService.updateNotifiedWarningPublicSentiment(openId, User.NotifiedSentiment.getInstance(sentiment));
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
	@PutMapping("insecureChangingNotifiedHotPublicSentiment")
	public ResponseEntity insecureChangingNotifiedHotPublicSentiment(@RequestParam String openId, @RequestParam String sentiment) {
		Assert.notNull(openId, "INSECURE_NOTIFIED_HOT_PUBLIC_SENTIMENT_USER_ID_IS_NULL");
		Assert.notNull(sentiment, "INSECURE_CHANGING_NOTIFIED_HOT_PUBLIC_SENTIMENT_SENTIMENT_IS_NULL");
		
		try {
			boolean data = userService.updateNotifiedHotPublicSentiment(openId, User.NotifiedSentiment.getInstance(sentiment));
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
	@PutMapping("insecureChangingNotifiedNegativePublicSentiment")
	public ResponseEntity insecureChangingNotifiedNegativePublicSentiment(@RequestParam String openId, @RequestParam String sentiment) {
		Assert.notNull(openId, "INSECURE_CHANGING_NOTIFIED_NEGATIVE_PUBLIC_SENTIMENT_USER_ID_IS_NULL");
		Assert.notNull(sentiment, "INSECURE_CHANGING_NOTIFIED_NEGATIVE_PUBLIC_SENTIMENT_SENTIMENT_IS_NULL");
		
		try {
			boolean data = userService.updateNotifiedNegativePublicSentiment(openId, User.NotifiedSentiment.getInstance(sentiment));
			return ResponseEntity.success("负面舆情接受状态修改成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**
	 * 为cloudUserId加密，返回后用于传递给客户端页面进行跨服务跳转
	 * @param cloudUserId 云用户的userId
	 * @return String 加密后的cloudUserId
	 */
	@GetMapping("encodedCloudUserId4Login")
	public ResponseEntity encodedCloudUserId(@RequestParam Long cloudUserId) {
		Assert.notNull(cloudUserId, "ENCODED_CLOUD_USER_ID_IS_NULL");
		
		try {
			String data = userService.encodeUserIdWithAES(cloudUserId);
			return ResponseEntity.success("加密云用户编号成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.UNKNOWN_ERROR;
		}
	}
	
	/**********************************************************************************************************************************/
	
//	@Autowired
//	private JobAndTriggerService jobAndTriggerService;
//	
//	@Autowired
//	private Scheduler scheduler; // 任务调度器
//	
//	/**
//	 * 增加任务
//	 * @param jobClassName 任务全路径名
//	 * @param jobGroupName 任务分组标识
//	 * @param cronExpression 任务执行周期表达式
//	 * @return null
//	 */
//	@PostMapping("/job/scheduling")
//	public ResponseEntity scheduling(@RequestParam String jobClassName, @RequestParam String jobGroupName, @RequestParam String cronExpression) {
//		try {
//			scheduler.start();
//			JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass()).withIdentity(jobClassName, jobGroupName).build();
//			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
//			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withSchedule(scheduleBuilder).build();
//			scheduler.scheduleJob(jobDetail, trigger);
//			return ResponseEntity.success("创建定时任务成功", null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.UNKNOWN_ERROR;
//		}
//	}
//	
//	/**
//	 * 暂停任务
//	 * @param jobClassName 任务全路径名
//	 * @param jobGroupName 任务分组标识
//	 * @return null
//	 */
//	@PutMapping("/job/pausing")
//	public ResponseEntity pausing(@RequestParam String jobClassName, @RequestParam String jobGroupName) {
//		try {
//			scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
//			return ResponseEntity.success("定时任务暂停成功", null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.UNKNOWN_ERROR;
//		}
//	}
//	
//	/**
//	 * 重启任务
//	 * @param jobClassName 任务全路径名
//	 * @param jobGroupName 任务分组标识
//	 * @return null
//	 */
//	@PutMapping("/job/resuming")
//	public ResponseEntity resuming(@RequestParam String jobClassName, @RequestParam String jobGroupName) {
//		try {
//			scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
//			return ResponseEntity.success("定时任务重启成功", null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.UNKNOWN_ERROR;
//		}
//	}
//	
//	/**
//	 * 重构任务
//	 * @param jobClassName 任务全路径名
//	 * @param jobGroupName 任务分组标识
//	 * @param cronExpression 任务执行周期表达式
//	 * @return null
//	 */
//	@PutMapping("/job/rescheduling")
//	public ResponseEntity rescheduling(@RequestParam String jobClassName, @RequestParam String jobGroupName, @RequestParam String cronExpression) {
//		try {
//			TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
//			// 重建表达式构建器
//			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
//			// 通过trigger标识获取之前任务的trigger实例
//			CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
//			// 按照新的表达式构建器来重新构建trigger
//			cronTrigger = cronTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
//			// 按照新的trigger重新设置任务的执行
//			scheduler.rescheduleJob(triggerKey, cronTrigger);
//			return ResponseEntity.success("更新定时任务成功", null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.UNKNOWN_ERROR;
//		}
//	}
//	
//	/**
//	 * 删除任务
//	 * @param jobClassName 任务全路径名
//	 * @param jobGroupName 任务分组标识
//	 * @return null
//	 */
//	@DeleteMapping("/job/deletion")
//	public ResponseEntity deletion(@RequestParam String jobClassName, @RequestParam String jobGroupName) {
//		try {
//			TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
//			scheduler.pauseTrigger(triggerKey);
//			scheduler.unscheduleJob(triggerKey);
//			scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
//			return ResponseEntity.success("删除任务成功", null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.UNKNOWN_ERROR;
//		}
//	}
//	
//	/**
//	 * 获取符合查询后的分页任务数据
//	 * @param map ->
//	 * 				conditions 每个key对应属性，每个value对应搜索内容
//	 * 				pageable key可以有page、size、sort和direction，具体value针对每个属性值
//	 * @return Page<User>
//	 */
//	@GetMapping("/job/page")
//	public ResponseEntity page(@RequestParam Map<String, Object> map) {
//		Assert.notNull(map, "PAGE_MAP_IS_NULL");
//		
//		try {
//			Page<JobAndTrigger> data =  jobAndTriggerService.get(PageUtils.parsePageable(JSON.parseObject(map.get("pageable").toString())), jobAndTriggerService.search(JSON.parseObject(map.get("conditions").toString())));
//			return ResponseEntity.success("获取分页任务信息成功", data);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.UNKNOWN_ERROR;
//		}
//	}
//	
//	private static QuartzJobBean getClass(String className) throws Exception {
//		Class<?> clazz = Class.forName(className);
//		return (QuartzJobBean) clazz.newInstance();
//	}
}
