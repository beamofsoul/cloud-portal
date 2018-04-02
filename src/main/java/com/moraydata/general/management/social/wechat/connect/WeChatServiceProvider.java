package com.moraydata.general.management.social.wechat.connect;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import com.moraydata.general.management.social.support.Constants;
import com.moraydata.general.management.social.wechat.api.WeChatService;
import com.moraydata.general.management.social.wechat.api.WeChatServiceImpl;

/**
 * Provider of WeChat OAuth2 processor, which will be invoked by Spring Social Connect System.
 * @author Mingshu Jian
 * @date 2018-03-28
 */
public class WeChatServiceProvider extends AbstractOAuth2ServiceProvider<WeChatService> {

	/**
	 * @param appId ID of third party application, which is registered from service provider 'WeChat'.
	 * @param appSecret Secret of third party application, which is registered from service provider 'WeChat'.
	 */
	public WeChatServiceProvider(String appId, String appSecret) {
		super(new WeChatOAuth2Template(appId, appSecret, Constants.WeChat.AUTHORIZE_URL, Constants.WeChat.ACCESS_TOKEN_URL));
	}

	@Override
	public WeChatService getApi(String accessToken) {
		return new WeChatServiceImpl(accessToken);
	}
}
