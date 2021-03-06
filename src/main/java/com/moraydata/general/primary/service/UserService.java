package com.moraydata.general.primary.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.primary.entity.InvitationCode;
import com.moraydata.general.primary.entity.Role;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;
import com.moraydata.general.primary.entity.dto.UserMiniInformation;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface UserService {
	
	User create(User instance);
	User update(User instance);
	long delete(Long... instanceIds) throws Exception;
	User get(Long instanceId);
	List<User> get(Long... instanceIds);
	Page<User> get(Pageable pageable);
	Page<User> get(Pageable pageable, Predicate predicate);
	List<User> get();
	BooleanExpression search(JSONObject conditions);
	
	User get(String username);
	boolean isUsernameUnique(String username, Long userId);
	boolean isNicknameUnique(String nickname, Long userId);
	
	boolean updatePassword(Long userId, String newPassword, String oldPassword);
	boolean updatePassword(String key, String code, String newPassword);
	boolean exists(String username, String phone);
	String sendMessageCode(String username, String phone, long currentClientMilliseconds);
	boolean bindParent(String invitationCode, Long currentUserId);
	
	/*******************************************************************************************************/
	
	boolean updatePassword(Long userId, String newPassword) throws Exception;
	boolean matchPassword(String rawPassword, String encodedPassword);
	String sendMessageCode4RetakingPassword(String username, String phone, Long currentClientMilliseconds) throws Exception;
	String sendMessageCode4ChangingPhone(String username, String phone, Long currentClientMilliseconds) throws Exception;
	String sendMessageCode4Registration(String phone, Long currentClientMilliseconds) throws Exception;
	boolean matchPasswordCode(String key, String code) throws Exception;
	boolean matchPhoneCode(String key, String code) throws Exception;
	boolean matchRegistrationCode(String key, String code) throws Exception;
	boolean updatePassword(String key, String newPassword) throws Exception;
	boolean updatePhone(String key, String phone) throws Exception;
	User update(User instance, User originalUser) throws Exception;
	InvitationCode getBindParentInvitationCode(String invitationCode) throws Exception;
	long bindParentByInvitationCode(InvitationCode instance, Long currentUserId) throws Exception;
	boolean updateUsernameAndPassword(Long userId, String username, String password) throws Exception;
	boolean updateOrderItemIds(Long userId, String orderItemIds) throws Exception;
	User getByOpenId(String openId) throws Exception;
	boolean exists(String phone) throws Exception;
	boolean existsByUsername(String username) throws Exception;
	boolean existsByUsernameAndPassword(String username, String password) throws Exception;
	User create(User instance, Role role) throws Exception;
	boolean isPhoneUnique(String phone, Long userId) throws Exception;
	boolean bindWeChat(String username, String openId) throws Exception;
	boolean isOpenIdUnique(String openId) throws Exception;
	boolean updateProfile(User user) throws Exception;
	boolean exists(Long userId) throws Exception;
	List<User> getByParentId(Long userId) throws Exception;
	Integer getCountOfInvitationCodes(Long userId) throws Exception;
	boolean decreaseCountOfInvitationCodes(Long userId, int many) throws Exception;
	boolean update(Long userId, String password, String phone, String description) throws Exception;
	boolean matchRelationship(Long userId, Long... userIds) throws Exception;
	boolean updateLevel(String openId, User.Level level) throws Exception;
	boolean updateLevel(Long userId, User.Level level) throws Exception;
	boolean updateNotified(String openId, Boolean notified) throws Exception;
	boolean updateNotified(Long userId, Boolean notified) throws Exception;
	boolean updateNotifiedWarningPublicSentiment(String openId, User.NotifiedSentiment sentiment) throws Exception;
	boolean updateNotifiedWarningPublicSentiment(Long userId, User.NotifiedSentiment sentiment) throws Exception;
	boolean updateNotifiedHotPublicSentiment(String openId, User.NotifiedSentiment sentiment) throws Exception;
	boolean updateNotifiedHotPublicSentiment(Long userId, User.NotifiedSentiment sentiment) throws Exception;
	boolean updateNotifiedNegativePublicSentiment(String openId, User.NotifiedSentiment sentiment) throws Exception;
	boolean updateNotifiedNegativePublicSentiment(Long userId, User.NotifiedSentiment sentiment) throws Exception;
	List<User> getWhoHasOpenId() throws Exception;
	List<UserBasicInformation> getAllIdAndUsernameWhoHasOpenId() throws Exception;
	List<UserBasicInformation> getLevelUserBasicInformation(Long level1UserId, User.Level... level);
	List<UserBasicInformation> getLevelUserBasicInformationByParentId(Long parentId, User.Level... level);
	List<UserMiniInformation> getAllIdAndUsername() throws Exception;
	String encodeUserIdWithAES(Long cloudUserId) throws Exception;
	boolean startTrial(Long userId) throws Exception;
}
