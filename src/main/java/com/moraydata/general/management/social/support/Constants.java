package com.moraydata.general.management.social.support;

public final class Constants {
	
	public static final class WeChat {
		
		// URL to get authorized code from WeChat.
		public final static String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect";
		
		// URL to get accessToken from WeChat.
		public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
		
		// URL to refresh accessToken from WeChat
		public static final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
		
		// URL to get user information from WeChat
		public static final String GET_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?openid= {openid}";
	}
}
