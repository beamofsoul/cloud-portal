package com.moraydata.general.primary.service.impl;

import static com.moraydata.general.management.util.BooleanExpressionUtils.addExpression;
import static com.moraydata.general.management.util.BooleanExpressionUtils.like;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toInteger;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLocalDateTime;
import static com.moraydata.general.management.util.BooleanExpressionUtils.toLong;
import static com.moraydata.general.management.util.ImageUtils.deletePhotoFile;
import static com.moraydata.general.management.util.ImageUtils.generateImageFilePath;
import static com.moraydata.general.management.util.ImageUtils.getClearPhotoString;
import static com.moraydata.general.management.util.ImageUtils.getPhotoType;
import static com.moraydata.general.management.util.ImageUtils.imageToBase64;
import static com.moraydata.general.management.util.ImageUtils.makePathEndWithDoubleSalsh;
import static com.moraydata.general.management.util.ImageUtils.serializeImageFromBase64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.annotation.Property;
import com.moraydata.general.management.message.MessageCodeManager;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.primary.entity.InvitationCode;
import com.moraydata.general.primary.entity.InvitationCode.Type;
import com.moraydata.general.primary.entity.Role;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.User.NotifiedSentiment;
import com.moraydata.general.primary.entity.UserRole;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;
import com.moraydata.general.primary.entity.query.QUser;
import com.moraydata.general.primary.repository.UserRepository;
import com.moraydata.general.primary.service.InvitationCodeService;
import com.moraydata.general.primary.service.LoginService;
import com.moraydata.general.primary.service.RoleService;
import com.moraydata.general.primary.service.UserRoleService;
import com.moraydata.general.primary.service.UserService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Property("project.user.photo-path")
	private static String USER_PHOTO_PATH;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MessageCodeManager messageCodeManager;
	
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	
	@Autowired
	private InvitationCodeService invitationCodeService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Transactional
	@Override
	public User create(User instance) {
		try {
			instance.setCountOfInvitationCodes(0);
			final String base64Photo = instance.getPhoto();
			if (StringUtils.isNotBlank(base64Photo)) {
				serializeUserPhoto(instance, base64Photo);
			}
			User savedUser = userRepository.save(instance);
			if (savedUser != null) {
				userRoleService.create(new UserRole(savedUser, roleService.get(Constants.ROLE.TRIAL_ROLE_NAME)));
			}
			return savedUser;
		} catch (Exception e) {
			log.error(String.format("An unknown error occurred, which caused persisting new user record failed", instance), e);
			return null;
		}
	}
	
	@Transactional
	@Override
	public User create(User instance, Role role) throws Exception {
		try {
			instance.setCountOfInvitationCodes(0);
			instance.setPassword(passwordEncoder.encode(instance.getPassword()));
			User savedUser = userRepository.save(instance);
			if (savedUser != null) {
				userRoleService.create(new UserRole(savedUser, role));
			}
			return savedUser;
		} catch (Exception e) {
			log.error(String.format("An unknown error occurred, which caused persisting new user record failed", instance), e);
			return null;
		}
	}

	@Override
	public User update(User instance) {
		User originalInstance = get(instance.getId());
		
		// if the updated user still has a photo
		final String base64Photo = instance.getPhotoString();
		if (StringUtils.isNotBlank(base64Photo)) {
			serializeUserPhoto(instance, base64Photo);
		} else {
			// delete photo image file once the updated user's photo has been removed by updating
			String originalInstancePhoto = originalInstance.getPhoto();
			if (StringUtils.isNotBlank(originalInstancePhoto)) {
				deletePhotoFile(generateImageFilePath(USER_PHOTO_PATH, originalInstancePhoto));
			}
		}
		
		instance.setRoles(originalInstance.getRoles());
		BeanUtils.copyProperties(instance, originalInstance);
		return userRepository.save(originalInstance);
	}

	private void serializeUserPhoto(User instance, final String base64Photo) {
		String photoContent = getClearPhotoString(base64Photo);
		String photoType = getPhotoType(base64Photo);
		String photoPath = generateImageFilePath(USER_PHOTO_PATH, instance.getUsername(), photoType);
		if (serializeImageFromBase64(photoContent, photoPath)) {
			instance.setPhoto(photoPath.replace(makePathEndWithDoubleSalsh(USER_PHOTO_PATH), ""));
			instance.setPhotoString(base64Photo);
		} else {
			throw new RuntimeException("failed to generage user's photo when trying to convert base64 code to image");
		}
	}

	@Transactional
	@Override
	public long delete(Long... instanceIds) throws Exception {
		List<User> users = this.get(instanceIds);
		users.stream().filter(e -> StringUtils.isNotBlank(e.getPhoto())).forEach(e -> deletePhotoFile(generateImageFilePath(USER_PHOTO_PATH, e.getPhoto())));
		// 删除所有用户登录记录
		loginService.deleteByUserIds(instanceIds);
		// 删除所有邀请码记录
		invitationCodeService.deleteByUserIds(instanceIds);
        return userRepository.deleteByIds(instanceIds);
	}

	@Transactional
	@Override
	public User get(Long instanceId) {
		User one = userRepository.findOne(instanceId);
		if (one != null) {
			Hibernate.initialize(one.getRoles());
			loadPhotoString(one);
		}
		return one;
	}

	@Override
	public List<User> get(Long... instanceIds) {
		return userRepository.findByIds(instanceIds);
	}

	@Override
	public Page<User> get(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public Page<User> get(Pageable pageable, Predicate predicate) {
		return userRepository.findAll(predicate, pageable);
	}

	@Override
	public List<User> get() {
		return userRepository.findAll();
	}

	@Override
	public BooleanExpression search(JSONObject conditions) {
		if (conditions == null) return null;

		QUser user = QUser.user;
		BooleanExpression exp = null;
		
		String id = conditions.getString(user.id.getMetadata().getName());
		exp = addExpression(id, exp, user.id.eq(toLong(id)));
		
		String nickname = conditions.getString(user.nickname.getMetadata().getName());
		exp = addExpression(nickname, exp, user.nickname.like(like(nickname)));
		
		String username = conditions.getString(user.username.getMetadata().getName());
		exp = addExpression(username, exp, user.username.like(like(username)));
		
		String password = conditions.getString(user.password.getMetadata().getName());
		exp = addExpression(password, exp, user.password.like(like(password)));
		
		String email = conditions.getString(user.email.getMetadata().getName());
		exp = addExpression(email, exp, user.email.like(like(email)));
		
		String phone = conditions.getString(user.phone.getMetadata().getName());
		exp = addExpression(phone, exp, user.phone.like(like(phone)));
		
		String status = conditions.getString(user.status.getMetadata().getName());
		exp = addExpression(status, exp, user.status.eq(toInteger(status)));
		
		String createdDate = conditions.getString(user.createdDate.getMetadata().getName());
		exp = addExpression(createdDate, exp, user.createdDate.before(toLocalDateTime(createdDate)));
		
		return exp;
	}

	/**
	 * Get unique user instance by user-name, otherwise return a null value.
	 * The method is used when user login. 
	 * @see AuthenticationUserDetailsService#getUser0
	 * @param username - user-name used to get unique user instance.
	 * @return an instance of user have gotten or a null value. 
	 */
	@Transactional
	@Override
	public User get(String username) {
		User currentUser = userRepository.findByUsername(username);
		if (currentUser != null) {
			Hibernate.initialize(currentUser.getRoles());
			loadPhotoString(currentUser);
		}
		return currentUser;
	}
	
	/**
	 * Determine whether the given user-name is unique in current database table.
	 * @param username - user-name used to check unique user-name.
	 * @param userId - check unique user-name excluded the user instance of the given userId.
	 * @return if the user-name is unique (excluded the user instance of the given userId) return true, otherwise return false.
	 */
	@Override
	public boolean isUsernameUnique(String username, Long userId) {
		BooleanExpression predicate = QUser.user.username.eq(username);
		if (userId != null) {
			predicate = predicate.and(QUser.user.id.ne(userId));
		}
		return userRepository.count(predicate) == 0;
	}
	
	/**
	 * Determine whether the given nickname is unique in current database table.
	 * @param nickname - nickname used to check unique nickname.
	 * @param userId - check unique nickname excluded the user instance of the given userId.
	 * @return if the nickname is unique (excluded the user instance of the given userId) return true, otherwise return false.
	 */
	@Override
	public boolean isNicknameUnique(String nickname, Long userId) {
		BooleanExpression predicate = QUser.user.nickname.eq(nickname);
		if (userId != null) {
			predicate = predicate.and(QUser.user.id.ne(userId));
		}
		return userRepository.count(predicate) == 0;
	}
	
	private void loadPhotoString(User instance) {
		try {
			String photoString = null;
			if (StringUtils.isNotBlank(instance.getPhoto())) {
				photoString = imageToBase64(generateImageFilePath(USER_PHOTO_PATH, instance.getPhoto()));
				instance.setPhotoString(photoString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional(readOnly = false)
	@Override
	public boolean updatePassword(Long userId, String newPassword, String oldPassword) {
		QUser $ = new QUser("User");
		BooleanExpression specificUser = $.id.eq(userId);
		// 1. Get original password (has been encoded) by given userId
		QueryResults<?> originalPasswordQueryResults = userRepository.findSpecificData(specificUser, $.password);
		if (originalPasswordQueryResults.isEmpty()) {
			log.error(String.format("Required user cannot be found by input userId: %s", userId));
			return false;
		}
		String originalPassword = ((Tuple) originalPasswordQueryResults.getResults().get(0)).get($.password);
		if (StringUtils.isBlank(originalPassword)) {
			log.error(String.format("Required user's password cannot be found correctly, the illegal password is ", originalPassword));
			return false;
		}
		// 2. Compare original password with oldPassword (uncoded)
		if (!passwordEncoder.matches(oldPassword, originalPassword)) {
			log.error("Original password and old password inputted are not matched");
			return false;
		}
		// 3. Update password to the new one
		String encodedNewPassword = passwordEncoder.encode(newPassword);
		long count = userRepository.update($.password, encodedNewPassword, specificUser);
		if (count != 1) {
			log.error(String.format("An unknown error occurred, which caused the return value of updating password SQL is not 1 but %s", count));
			return false;
		}
		log.info(String.format("The password of user[%s] has been updated successfully. The new password is %s", userId, newPassword));
		return true;
	}

	@Override
	public boolean exists(String username, String phone) {
		QUser $ = QUser.user;
		return userRepository.exists($.username.eq(username).and($.phone.eq(phone))); 
	}
	
	@Override
	public boolean existsByUsername(String username) {
		QUser $ = QUser.user;
		return userRepository.exists($.username.eq(username)); 
	}
	
	@Override
	public String sendMessageCode(String username, String phone, long currentClientMilliseconds) {
		// 1. Check whether username and phone are matched
		boolean exists = exists(username, phone);
		if (!exists) {
			log.error(String.format("Given username[%s] and phone[%s] are not matched", username, phone));
			return null;
		}
		// 2. Send user a message code
		Integer code = null;
		try {
			code = messageCodeManager.send(phone);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 3. Save message code into Redis cache for further checking
		if (code == null) {
			log.error(String.format("An unknown error occurred, that causes the operation of sending message code[%s] to phone[%s] of user[%s] failed", code, phone, username));
			return null;
		}
		
		long expiredDate = messageCodeManager.getExpiredDate(currentClientMilliseconds);
		String key = String.format("messageCode:%s#%s#%s", username, phone, expiredDate);
		redisTemplate.opsForValue().set(key, code, messageCodeManager.getTime2Live(), messageCodeManager.getTimeUnit());
		
		log.info(String.format("A message code[%s] has been sent to phone[%s] of user[%s] and the expired date is %s", code, phone, username, new Date(expiredDate)));
		return key;
	}

	@Override
	public boolean updatePassword(String key, String code, String newPassword) {
		// 1. Get code by key
		Object storedCode = redisTemplate.opsForValue().get(key);
		if (storedCode == null) {
			log.error(String.format("Cannot get relative message code by given key[%s], or the message code[%s] related to given key has been expired", key, code));
			return false;
		}
		// 2. Check codes are matched
		if (!storedCode.equals(code)) {
			log.error(String.format("The typed message code[%s] and the stored message code[%s] for key[%s] are not matched", code, storedCode, key));
			return false;
		}
		// 3. Updating new password
		QUser $ = QUser.user;
		String[] keyParser = key.substring(key.indexOf(":")).split("#");
		long updatedRecords = userRepository.update($.password, newPassword, $.username.eq(keyParser[0]).and($.phone.eq(keyParser[1])));
		if (updatedRecords > 0) {
			log.info("The password of user[%s] has been updated by given message code[%s]", keyParser[0], code); 
			return true;
		}
		log.error(String.format("An unknown error occurred, that causes the operation of updating password by message code[%s] to phone[%s] of user[%s] failed", code, keyParser[1], keyParser[0]));
		return false;
	}
	
	@Transactional
	@Override
	public boolean bindParent(String invitationCode, Long currentUserId) {
		// 1. Get invitationCode information
		InvitationCode instance = getBindParentInvitationCode(invitationCode);
		if (instance == null) {
			log.error(String.format("Invalid invitation code[%s]", invitationCode));
			return false;
		} else if (!instance.getAvailable()) {
			log.error(String.format("Current invitation code[%s] is unavailable", invitationCode));
			return false;
		}
		// 2. bind user parent id by current user id
		QUser $ = QUser.user;
		Long parentUserId = instance.getUserId();
		long updatedRecords = userRepository.update($.parentId, parentUserId, $.id.eq(currentUserId));
		if (updatedRecords > 0) {
			invitationCodeService.delete(invitationCode);
			log.info(String.format("A parent user[$s] has been bound to current user[%s]", parentUserId, currentUserId));
			return true;
		} else {
			log.error(String.format("An unknown error occurred, that causes the operation of binding parent user by invitation code[%s] for current user[%s] failed", invitationCode, currentUserId));
			return false;
		}
	}
	
	/*****************************************************************************************************************************************************************/

	/**
	 * For Open API
	 * @param userId
	 * @param newPassword
	 * @return boolean
	 * @exception Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean updatePassword(Long userId, String newPassword) throws Exception {
		QUser $ = new QUser("User");
		String encodedNewPassword = passwordEncoder.encode(newPassword);
		return userRepository.update($.password, encodedNewPassword, $.id.eq(userId)) > 0;
	}
	
	/**
	 * For Open API
	 * @param rawPassword
	 * @param encodedPassword
	 * @return boolean
	 */
	@Override
	public boolean matchPassword(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
	
	/**
	 * For Open API
	 * @param username
	 * @param phone
	 * @param currentClientMilliseconds
	 * @return key
	 * @throws Exception
	 */
	@Override
	public String sendMessageCode4RetakingPassword(String username, String phone, Long currentClientMilliseconds) throws Exception {
		Integer code = messageCodeManager.send(phone);
		if (code == null) {
			// 发送失败，可能是由于流量控制或其他原因
			return null;
		}
		long expiredDate = messageCodeManager.getExpiredDate(currentClientMilliseconds);
		String key = String.format(Constants.SENDING_MESSAGE_CODE.DEFAULT_MESSAGE_CODE_FORMAT, username, phone, expiredDate);
		redisTemplate.opsForValue().set(key, code, messageCodeManager.getTime2Live(), messageCodeManager.getTimeUnit());
		return key;
	}
	
	/**
	 * For Open API
	 * @param username
	 * @param phone
	 * @param currentClientMilliseconds
	 * @return key
	 * @throws Exception
	 */
	@Override
	public String sendMessageCode4ChangingPhone(String username, String phone, Long currentClientMilliseconds) throws Exception {
		Integer code = messageCodeManager.send(phone);
		long expiredDate = messageCodeManager.getExpiredDate(currentClientMilliseconds);
		String key = String.format(Constants.SENDING_MESSAGE_CODE.DEFAULT_MESSAGE_CODE_FORMAT, username, phone, expiredDate);
		redisTemplate.opsForValue().set(key, code, messageCodeManager.getTime2Live(), messageCodeManager.getTimeUnit());
		return key;
	}
	
	/**
	 * For Open API
	 * @param phone
	 * @param currentClientMilliseconds
	 * @return key
	 * @throws Exception
	 */
	@Override
	public String sendMessageCode4Registration(String phone, Long currentClientMilliseconds) throws Exception {
		Integer code = messageCodeManager.send(phone);
		long expiredDate = messageCodeManager.getExpiredDate(currentClientMilliseconds);
		String key = String.format(Constants.SENDING_MESSAGE_CODE.DEFAULT_MESSAGE_CODE_FORMAT, Constants.SENDING_MESSAGE_CODE.REGISTRATION_KEY_PREFIX, phone, expiredDate);
		redisTemplate.opsForValue().set(key, code, messageCodeManager.getTime2Live(), messageCodeManager.getTimeUnit());
		return key;
	}

	/**
	 * For Open API
	 * @param key
	 * @param code
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean matchPasswordCode(String key, String code) throws Exception {
		Object storedCode = redisTemplate.opsForValue().get(key);
		return (storedCode != null && storedCode.toString().equals(code));
	}
	
	/**
	 * For Open API
	 * @param key
	 * @param code
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean matchPhoneCode(String key, String code) throws Exception {
		Object storedCode = redisTemplate.opsForValue().get(key);
		return (storedCode != null && storedCode.toString().equals(code));
	}
	
	/**
	 * For Open API
	 * @param key
	 * @param code
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean matchRegistrationCode(String key, String code) throws Exception {
		Object storedCode = redisTemplate.opsForValue().get(key);
		return (storedCode != null && storedCode.toString().equals(code));
	}
	
	/**
	 * For Open API
	 * @param key
	 * @param newPassword
	 * @return boolean
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean updatePassword(String key, String newPassword) throws Exception {
		QUser $ = new QUser("User");
		String[] keyParser = key.substring(key.indexOf(":") + 1).split("#");
		return userRepository.update($.password, passwordEncoder.encode(newPassword), $.username.eq(keyParser[0]).and($.phone.eq(keyParser[1]))) > 0;
	}
	
	/**
	 * For Open API
	 * @param key
	 * @param phone
	 * @return boolean
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean updatePhone(String key, String phone) throws Exception {
		QUser $ = new QUser("User");
		String[] keyParser = key.substring(key.indexOf(":") + 1).split("#");
		return userRepository.update($.phone, phone, $.username.eq(keyParser[0])) > 0;
	}
	
	/**
	 * For Open API
	 * @param user
	 * @param originalUser
	 * @return User
	 * @throws Expcetion
	 */
	@Override
	public User update(User user, User originalUser) throws Exception {
		user.setRoles(originalUser.getRoles());
		BeanUtils.copyProperties(user, originalUser);
		return userRepository.save(originalUser);
	}
	
	/**
	 * For Open API
	 * @param invitationCode
	 * @return InvitationCode
	 * @throws Exception
	 */
	@Override
	public InvitationCode getBindParentInvitationCode(String invitationCode) {
		return invitationCodeService.get(invitationCode, Type.BIND_PARENT_USER_ID);
	}
	
	/**
	 * For Open API
	 * @param instance
	 * @param currentUserId
	 * @return long
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public long bindParentByInvitationCode(InvitationCode instance, Long currentUserId) throws Exception {
		QUser $ = new QUser("User");
		Long parentUserId = instance.getUserId();
		long updatedRecords = userRepository.update($.parentId, parentUserId, $.id.eq(currentUserId));
		if (updatedRecords > 0) {
			// 删除已使用的邀请码
			invitationCodeService.delete(instance.getCode());
		}
		return updatedRecords;
	}
	
	/**
	 * For Open API
	 * @param userId
	 * @param username
	 * @param password
	 * @return boolean
	 * @throws Exception
	 */
	@Transactional
	@Override
	public boolean updateUsernameAndPassword(Long userId, String username, String password) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update(Arrays.asList($.username, $.password) , Arrays.asList(username, passwordEncoder.encode(password)), $.id.eq(userId)) > 0;
	}
	
	/**
	 * For Open API
	 * @param userId
	 * @param orderItemIds
	 * @return boolean
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean updateOrderItemIds(Long userId, String orderItemIds) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.orderItemIds, orderItemIds, $.id.eq(userId)) > 0;
	}

	/**
	 * For Open API -> Wechat
	 * @param openId
	 * @return User
	 * @throws Exception
	 */
	@Transactional
	@Override
	public User getByOpenId(String openId) throws Exception {
		User user = userRepository.findByOpenId(openId);
		if (user != null) {
			Hibernate.initialize(user.getRoles());
		}
		return user;
	}
	
	/**
	 * For Open API
	 * @param phone
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean exists(String phone) throws Exception {
		QUser $ = QUser.user;
		return userRepository.exists($.phone.eq(phone)); 
	}
	
	/**
	 * For Open API
	 * @param phone
	 * @param userId
	 * @throws Exception
	 */
	@Override
	public boolean isPhoneUnique(String phone, Long userId) {
		BooleanExpression predicate = QUser.user.phone.eq(phone);
		if (userId != null) {
			predicate = predicate.and(QUser.user.id.ne(userId));
		}
		return userRepository.count(predicate) == 0;
	}

	/**
	 * For Open API
	 * @param username
	 * @param openId
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean bindWeChat(String username, String openId) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.openId, openId, $.username.eq(username)) > 0;
	}

	/**
	 * For Open API
	 * @param openId
	 * @throws Exception
	 */
	@Override
	public boolean isOpenIdUnique(String openId) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.exists($.openId.eq(openId));
//		return userRepository.count($.openId.eq(openId)) == 0;
	}

	/**
	 * For Open API
	 * @param user
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean updateProfile(User user) throws Exception {
		QUser $ = new QUser("User");
		List<Path<?>> path = new ArrayList<>();
		List<String> value = new ArrayList<>();
		if (user.getNickname() != null) {
			path.add($.nickname);
			value.add(user.getNickname());
		}
		if (user.getCompany() != null) {
			path.add($.company);
			value.add(user.getCompany());
		}
		if (user.getCompanyTitle() != null) {
			path.add($.companyTitle);
			value.add(user.getCompanyTitle());
		}
		if (user.getCompanyLocation() != null) {
			path.add($.companyLocation);
			value.add(user.getCompanyLocation());
		}
		if (user.getCompanyType() != null) {
			path.add($.companyType);
			value.add(user.getCompanyType());
		}
		if (user.getCompanyPhone() != null) {
			path.add($.companyPhone);
			value.add(user.getCompanyPhone());
		}
		if (user.getCompanyFax() != null) {
			path.add($.companyFax);
			value.add(user.getCompanyFax());
		}
		if (user.getDescription() != null) {
			path.add($.description);
			value.add(user.getDescription());
		}
		return path.isEmpty() ? false : userRepository.update(path, value, $.id.eq(user.getId())) > 0;
	}

	@Override
	public boolean exists(Long userId) throws Exception {
		return userRepository.existsById(userId);
	}

	/**
	 * For Open API
	 * @param userId
	 * @throws Exception
	 */
	@Override
	public List<User> getByParentId(Long userId) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.findByPredicate($.parentId.eq(userId));
	}
	
	/**
	 * For Open API
	 * @param userId
	 * @return Integer
	 * @throws Exception
	 */
	@Override
	public Integer getCountOfInvitationCodes(Long userId) throws Exception {
		User currentUser = this.get(userId);
		if (currentUser == null) {
			return null;
		} else {
			return currentUser.getCountOfInvitationCodes();
		}
	}

	/**
	 * For Open API
	 * @param userId
	 * @param many
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean decreaseCountOfInvitationCodes(Long userId, int many) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.countOfInvitationCodes, $.countOfInvitationCodes.subtract(many), $.id.eq(userId)) > 0;
	}
	
	/**
	 * For Open API
	 * @param userId
	 * @param password
	 * @param phone
	 * @param description
	 * @return boolean
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	@Override
	public boolean update(Long userId, String password, String phone, String description) throws Exception {
		QUser $ = new QUser("User");
		List<Path<?>> path = new ArrayList<>();
		List<String> value = new ArrayList<>();
		if (StringUtils.isNotBlank(password)) {
			path.add($.password);
			value.add(passwordEncoder.encode(password));
		}
		if (StringUtils.isNotBlank(phone)) {
			path.add($.phone);
			value.add(phone);
		}
		if (StringUtils.isNotBlank(description)) {
			path.add($.description);
			value.add(description);
		}
		return userRepository.update(path, value, $.id.eq(userId)) > 0;
	}

	/**
	 * For Open API
	 * @param userId
	 * @param userIds
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean matchRelationship(Long userId, Long... userIds) throws Exception {
		QUser $ = QUser.user;
		return userRepository.count($.id.in(userIds).and($.parentId.eq(userId))) == userIds.length; 
	}
	
	@Transactional(readOnly = false)
	@Override
	public boolean updateLevel(String openId, User.Level level) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.level, level.getValue(), $.openId.eq(openId)) > 0;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updateLevel(Long userId, User.Level level) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.level, level.getValue(), $.id.eq(userId)) > 0;
	}
	
	@Transactional(readOnly = false)
	@Override
	public boolean updateNotified(String openId, Boolean notified) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notified, notified, $.openId.eq(openId)) > 0;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updateNotified(Long userId, Boolean notified) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notified, notified, $.id.eq(userId)) > 0;
	}
	
	@Transactional(readOnly = false)
	@Override
	public boolean updateNotifiedWarningPublicSentiment(String openId, NotifiedSentiment sentiment) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notifiedWarningPublicSentiment, sentiment.getValue(), $.openId.eq(openId)) > 0;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updateNotifiedWarningPublicSentiment(Long userId, NotifiedSentiment sentiment) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notifiedWarningPublicSentiment, sentiment.getValue(), $.id.eq(userId)) > 0;
	}
	
	@Transactional(readOnly = false)
	@Override
	public boolean updateNotifiedHotPublicSentiment(String openId, NotifiedSentiment sentiment) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notifiedHotPublicSentiment, sentiment.getValue(), $.openId.eq(openId)) > 0;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updateNotifiedHotPublicSentiment(Long userId, NotifiedSentiment sentiment) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notifiedHotPublicSentiment, sentiment.getValue(), $.id.eq(userId)) > 0;
	}
	
	@Transactional(readOnly = false)
	@Override
	public boolean updateNotifiedNegativePublicSentiment(String openId, NotifiedSentiment sentiment) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notifiedNegativePublicSentiment, sentiment.getValue(), $.openId.eq(openId)) > 0;
	}

	@Transactional(readOnly = false)
	@Override
	public boolean updateNotifiedNegativePublicSentiment(Long userId, NotifiedSentiment sentiment) throws Exception {
		QUser $ = new QUser("User");
		return userRepository.update($.notifiedNegativePublicSentiment, sentiment.getValue(), $.id.eq(userId)) > 0;
	}

	@Override
	public List<User> getWhoHasOpenId() throws Exception {
		QUser $ = new QUser("User");
		return userRepository.findByPredicate($.openId.isNotNull());
	}

	@Override
	public List<UserBasicInformation> getAllIdAndUsernameWhoHasOpenId() throws Exception {
		return userRepository.findAllIdAndUsernameWhoHasOpenId();
	}
	
	/**
	 * 通过输入的1级用户的userId，找到其主账号(包括主账号)所辖所有2或3级设置了接收推送消息的子账号
	 * PS: 1级用户未必是主账号
	 */
	@Override
	public List<UserBasicInformation> getLevelUserBasicInformation(Long level1UserId, User.Level... level) {
		QUser $ = new QUser("User");
		QueryResults<?> queryResult = userRepository.findSpecificData($.id.eq(level1UserId), $.parentId);
		Long level1UserParentId = ((Tuple) queryResult.getResults().get(0)).get($.parentId);
		Long masterUserId = (level1UserParentId == null || level1UserParentId == 0) ? level1UserId : level1UserParentId;
		
		int[] levels = null;
		if (level != null && level.length > 0) {
			levels = new int[level.length];
			for (int i = 0; i < level.length; i++) {
				levels[i] = level[i].getValue();
			}
		} else {
			return null;
		}
		
		return userRepository.findLevelUserBasicInformation(masterUserId, levels);
	}

//	/**
//	 * 通过输入的1级用户的userId，找到其主账号(包括主账号)所辖所有3级设置了接收推送消息的子账号
//	 * PS: 1级用户未必是主账号
//	 */
//	@Override
//	public List<UserBasicInformation> getLevel3UserBasicInformation(Long level1UserId) throws Exception {
//		// 1. 找到当前1级用户的主账号Id
//		QUser $ = new QUser("User");
//		QueryResults<?> queryResult = userRepository.findSpecificData($.id.eq(level1UserId), $.parentId);
//		Long level1UserParentId = ((Tuple) queryResult.getResults().get(0)).get($.parentId);
//		Long masterUserId = (level1UserParentId == null || level1UserParentId == 0) ? level1UserId : level1UserParentId;
//		
//		// 2. 找到主账号下所有3级子账号
//		return userRepository.findLevelUserBasicInformation(masterUserId, User.Level.THIRD.getValue());
////		return userRepository.findLevel3UserBasicInformation(masterUserId);
//	}
//	
//	/**
//	 * 通过输入的1级用户的userId，找到其主账号(包括主账号)所辖所有2级设置了接收推送消息的子账号
//	 * PS: 1级用户未必是主账号
//	 */
//	@Override
//	public List<UserBasicInformation> getLevel2UserBasicInformation(Long level1UserId) throws Exception {
//		// 1. 找到当前1级用户的主账号Id
//		QUser $ = new QUser("User");
//		QueryResults<?> queryResult = userRepository.findSpecificData($.id.eq(level1UserId), $.parentId);
//		Long level1UserParentId = ((Tuple) queryResult.getResults().get(0)).get($.parentId);
//		Long masterUserId = (level1UserParentId == null || level1UserParentId == 0) ? level1UserId : level1UserParentId;
//		
//		// 2. 找到主账号下所有3级子账号
//		return userRepository.findLevelUserBasicInformation(masterUserId, User.Level.SECOND.getValue());
////		return userRepository.findLevel2UserBasicInformation(masterUserId);
//	}
//	
//	/**
//	 * 通过输入的1级用户的userId，找到其主账号(包括主账号)所辖所有2和3级设置了接收推送消息的子账号
//	 * PS: 1级用户未必是主账号
//	 */
//	@Override
//	public List<UserBasicInformation> getLevel2Or3UserBasicInformation(Long level1UserId) throws Exception {
//		// 1. 找到当前1级用户的主账号Id
//		QUser $ = new QUser("User");
//		QueryResults<?> queryResult = userRepository.findSpecificData($.id.eq(level1UserId), $.parentId);
//		Long level1UserParentId = ((Tuple) queryResult.getResults().get(0)).get($.parentId);
//		Long masterUserId = (level1UserParentId == null || level1UserParentId == 0) ? level1UserId : level1UserParentId;
//		
//		// 2. 找到主账号下所有2和3级子账号
//		return userRepository.findLevel2Or3UserBasicInformation(masterUserId);
//	}
}
