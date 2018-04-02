package com.moraydata.general.management.social.wechat.api;

/**
 * A interface to invoke and return WeChat user information
 */
public interface WeChatService {
	
	/**
	 * @Title: getUserInfo  
	 * @Description: get WeChat user information by an input openId
	 * @param openId
	 * @return WeChatUser instance has been gotten
	 */
	WeChatUser getUserProfile(String openId);
}
