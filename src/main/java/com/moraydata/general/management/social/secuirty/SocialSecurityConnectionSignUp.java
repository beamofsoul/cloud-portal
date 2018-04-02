package com.moraydata.general.management.social.secuirty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import com.moraydata.general.management.social.wechat.api.WeChatUser;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * Automatic sign-up under third party authorization
 * @author Mingshu Jian
 * @date 2018-03-27 10:02
 */
@Slf4j
public class SocialSecurityConnectionSignUp implements ConnectionSignUp {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${project.base.social.provider}")
	String provider;
	
	@Value("${project.base.social.user.defaultPassword}")
	String defaultPassword;

	/**
	 * Create a new user using given social user information, and return unique user id
	 */
	@Override
	public String execute(Connection<?> connection) {
		final UserProfile profile = connection.fetchUserProfile();
		final String providerId = connection.getKey().getProviderId();
		User user = null;
		
		if (StringUtils.isNotBlank(provider) && provider.equals(providerId)) {
			user = getUserInstance(profile, user);
		} else {
			throw new RuntimeException(String.format("connected to unknown provider %s", providerId));
		}
		return user != null ? String.valueOf(user.getId()) : null;
	}

	private User getUserInstance(final UserProfile profile, User user) {
		WeChatUser userInfo = (WeChatUser) profile;
		boolean usernameUnique = userService.isUsernameUnique(userInfo.getOpenid(), null);
		if (usernameUnique) {
			user = userService.create(User.builder()
				.username(userInfo.getOpenid())
				.password(passwordEncoder.encode(defaultPassword))
				.nickname(userInfo.getNickname())
				.email(userInfo.getEmail())
				.avatarUrl(userInfo.getHeadimgurl())
				.status(User.Status.NORMAL.getValue())
				.build());
			log.info(String.format("A new user instance that is via scanning qrcode of third party authorization has been created. Its' unique id is %s, and username is %s", user.getId(), user.getUsername()));
		} else {
			user = userService.get(userInfo.getOpenid());
			log.info(String.format("A user instance that is via scanning qrcode of third party authorization has been gotten from persistent storage. Its' unique id is %s, and username is %s", user.getId(), user.getUsername()));
		}
		return user;
	}
}
