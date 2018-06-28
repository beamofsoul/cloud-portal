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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public final class WeChatTokenHelper {
	
	@Getter
	@Value("${project.base.wechat.service.appId}")
	private String appId;
	
	@Getter
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
		redisTemplate.opsForValue().set(Constants.WECHAT.SERVICE_ACCESS_TOKEN, JSON.toJSONString(token), token.getExpiresIn() - 1, TimeUnit.SECONDS);
	}
	
	public void refreshToken() {
		redisTemplate.delete(Constants.WECHAT.SERVICE_ACCESS_TOKEN);
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	private Token getAccessTokenResponse() {
		String integratedUrl = HttpUrlUtils.integrate(this.accessTokenUrl, new HashMap<String, Object>() {{
			put("grant_type", "client_credential");
			put("appid", appId);
			put("secret", appSecret);
		}});
		Map<String, Object> response =  RestTemplateUtils.INSTANCE.getRestTemplate().getForObject(integratedUrl, Map.class);
		if (response.containsKey(Constants.WECHAT.ERROR_MEESSAGE_KEY)) {
			// 与微信通讯过程中存在异常
			log.error(String.format("尝试向微信服务器获取 access_token 过程中发生异常: %s", JSON.toJSONString(response)));
			return null;
		}
		return Token.builder().accessToken(response.get(Constants.WECHAT.ACCESS_TOKEN).toString()).expiresIn(Long.valueOf(response.get(Constants.WECHAT.EXPIRES_IN).toString())).maketime(new Date().getTime()).build(); 
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
