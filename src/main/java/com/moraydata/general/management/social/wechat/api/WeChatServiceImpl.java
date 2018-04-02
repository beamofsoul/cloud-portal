package com.moraydata.general.management.social.wechat.api;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import com.moraydata.general.management.social.support.Constants;

/**
 * The implementation of WeChat OAuth2 authorization API
 */
public class WeChatServiceImpl extends AbstractOAuth2ApiBinding implements WeChatService {

	/**
	 * Set token strategy which will put accessToken in request parameters instead of the default strategy that putting it into HTTP Headers
	 * @param accessToken
	 */
	public WeChatServiceImpl(String accessToken) {
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
	}

	/**
	 * 默认注册的StringHttpMessageConverter字符集为ISO-8859-1，而微信返回的是UTF-8的，所以覆盖了原来的方法。
	 */
	@Override
	protected List<HttpMessageConverter<?>> getMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
		messageConverters.set(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//		messageConverters.remove(0);
//		messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		messageConverters.add(new MappingJackson2HttpMessageConverter() {
			@Override
			public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
				List<MediaType> mediaTypes = new ArrayList<>();
				mediaTypes.add(MediaType.TEXT_PLAIN);
				super.setSupportedMediaTypes(mediaTypes);
			}
		});
		return messageConverters;
	}

	@SuppressWarnings("serial")
	@Override
	public WeChatUser getUserProfile(String openId) {
		return getRestTemplate().getForObject(Constants.WeChat.GET_USER_INFO_URL, WeChatUser.class, new HashMap<String, String>() {{put("openid", openId);}});
	}
}
