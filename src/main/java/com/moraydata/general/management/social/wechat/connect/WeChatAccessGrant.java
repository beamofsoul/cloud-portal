package com.moraydata.general.management.social.wechat.connect;

import org.springframework.social.oauth2.AccessGrant;

import lombok.Getter;
import lombok.Setter;

/**
 * OAuth2 access token of WeChat.
 * This is different compared with standard OAuth2 protocol.
 * It returns openId at the same time when WeChat returns access_token.
 * There is no separate service which using access_token to exchange openId.
 * So that here just extends standard AccessGrant, and add a field 'openId' which will be used and encapsulated as a part of returned access_token information.
 * @author Mingshu Jian
 * @date 2018-03-28
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class WeChatAccessGrant extends AccessGrant {

	private String openId;

	public WeChatAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn, String openId) {
		super(accessToken, scope, refreshToken, expiresIn);
		this.openId = openId;
	}
}
