package com.moraydata.general.management.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public final class WeChatTokenHelper {
	
	@Value("${project.base.wechat.service.appId}")
	private String appId;
	
	@Value("${project.base.wechat.service.appSecret}")
	private String appSecret;
	
	@Value("${project.base.wechat.service.accessTokenUrl}")
	private String accessTokenUrl;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public Token getToken() {
		// 判断redis中是否有微信服务号的access_token，如果有则直接返回
		Object tokenObject = redisTemplate.opsForValue().get(Constants.WECHAT.SERVICE_ACCESS_TOKEN);
		if (tokenObject != null) {
			return JSONObject.parseObject(tokenObject.toString(), Token.class);
		}
		// 如果没有则通过url取一个
		Token token = getAccessTokenResponse();
		saveToken(token);
		return token;
	}
	
	private void saveToken(Token token) {
		redisTemplate.opsForValue().set(Constants.WECHAT.SERVICE_ACCESS_TOKEN, JSON.toJSONString(token), token.getExpiresIn(), TimeUnit.SECONDS);
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	private Token getAccessTokenResponse() {
		String integratedUrl = HttpUrlUtils.integrate(this.accessTokenUrl, new HashMap<String, Object>() {{
			put("grant_type", "client_credential");
			put("appid", appId);
			put("secret", appSecret);
		}});
		Map<String, Object> response =  RestTemplateUtils.INSTANCE.getRestTemplate().getForObject(integratedUrl, Map.class);
		return Token.builder().accessToken(response.get("access_token").toString()).expiresIn(Long.valueOf(response.get("expires_in").toString())).maketime(new Date().getTime()).build(); 
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@Builder
	public static final class Token {
		private String accessToken;
		private long expiresIn;
		private long maketime;
	}
}
