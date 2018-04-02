package com.moraydata.general.management.social.wechat.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

import com.moraydata.general.management.social.wechat.api.WeChatService;

/**
 * Factory for creating OAuth2-based {@link Connection}s.
 * @author Mingshu Jian  
 * @date 2018-03-28
 */
public class WeChatConnectionFactory extends OAuth2ConnectionFactory<WeChatService> {
	
	public WeChatConnectionFactory(String providerId, String appId, String appSecret) {
		super(providerId, new WeChatServiceProvider(appId, appSecret), new WeChatServiceAdapter());
	}
	
	/**
	 * Extract provider user id from accessGrant directly on account of that WeChat returns openId and accessToken together.
	 */
	@Override
	protected String extractProviderUserId(AccessGrant accessGrant) {
		if(accessGrant instanceof WeChatAccessGrant) {
			return ((WeChatAccessGrant)accessGrant).getOpenId();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.social.connect.support.OAuth2ConnectionFactory#createConnection(org.springframework.social.oauth2.AccessGrant)
	 */
	@Override
	public Connection<WeChatService> createConnection(AccessGrant accessGrant) {
		return new OAuth2Connection<WeChatService>(getProviderId(), extractProviderUserId(accessGrant), accessGrant.getAccessToken(),
				accessGrant.getRefreshToken(), accessGrant.getExpireTime(), getOAuth2ServiceProvider(), getApiAdapter(extractProviderUserId(accessGrant)));
	}

	/* (non-Javadoc)
	 * @see org.springframework.social.connect.support.OAuth2ConnectionFactory#createConnection(org.springframework.social.connect.ConnectionData)
	 */
	@Override
	public Connection<WeChatService> createConnection(ConnectionData data) {
		return new OAuth2Connection<WeChatService>(data, getOAuth2ServiceProvider(), getApiAdapter(data.getProviderUserId()));
	}
	
	private ApiAdapter<WeChatService> getApiAdapter(String providerUserId) {
		return new WeChatServiceAdapter(providerUserId);
	}
	
	private OAuth2ServiceProvider<WeChatService> getOAuth2ServiceProvider() {
		return (OAuth2ServiceProvider<WeChatService>) getServiceProvider();
	}
}
