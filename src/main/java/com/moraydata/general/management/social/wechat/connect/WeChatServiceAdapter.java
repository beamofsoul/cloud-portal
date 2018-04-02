package com.moraydata.general.management.social.wechat.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.moraydata.general.management.social.wechat.api.WeChatService;
import com.moraydata.general.management.social.wechat.api.WeChatUser;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * An adapter that bridges between the uniform {@link Connection} model and WeChat API model.
 * @author Mingshu Jian
 * @date 2018-03-28
 */
@NoArgsConstructor
@AllArgsConstructor
public class WeChatServiceAdapter implements ApiAdapter<WeChatService> {

	private String openId;

	@Override
	public boolean test(WeChatService api) {
		return true;
	}

	@Override
	public void setConnectionValues(WeChatService api, ConnectionValues values) {
		WeChatUser profile = api.getUserProfile(openId);
		values.setProviderUserId(profile.getOpenid());
		values.setDisplayName(profile.getNickname());
		values.setImageUrl(profile.getHeadimgurl());
	}

	@Override
	public UserProfile fetchUserProfile(WeChatService api) {
		return api.getUserProfile(openId);
	}

	@Override
	public void updateStatus(WeChatService api, String message) {
		// do nothing
	}
}
