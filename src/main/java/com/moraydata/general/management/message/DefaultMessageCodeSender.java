package com.moraydata.general.management.message;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Default way to send given user a message code. 
 * @author Mingshu Jian  
 * @date 2018-03-30
 */
@Component
final class DefaultMessageCodeSender implements MessageCodeSender {
	
	private final String requestUrl = "http://api.feige.ee/SmsService/Send";
	private final String account = "18600574873";
	private final String password = "7f9d61947e5c6950edba933eb";
	private final String signId = "40796";
	
	private RestTemplate template;

	@Override
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	public boolean send(String phone, int code, String content) throws InterruptedException, ExecutionException {
		ResponseEntity<Map> responseEntity = getRestTemplate().exchange(requestUrl, HttpMethod.POST, new HttpEntity(new HashMap<String, Object>() {{
			put("Account", account);
			put("Pwd", password);
			put("Content", String.format(content, code));
			put("Mobile", phone);
			put("SignId", signId);
		}}), Map.class);
		Map body = responseEntity.getBody();
		
//		SendId:2018032914390368030138724    // 短信回执编号，为唯一识别码，用户可通过此编号获取记录详情
//		InvalidCount:0                      // 无效号码（可能是非手机号码如02161531765）
//		SuccessCount:1                      // 成功数量（一般为该批次计费条数）
//		BlackCount:0                        // 黑名单号码（一般为该批次计费条数）
//		Code:0                              // 短信回执状态码，判断成功失败的标志（成功为0，其他请参照 API 错误代码）
//		Message:OK                          // 短信回执状态描述（成功为ok，其它请参考 API 错误代码）
		
		return "ok".equals(body.get("Message"));
	}
	
	private RestTemplate getRestTemplate() {
		if (this.template == null) {
			RestTemplate template = new RestTemplate();
			template.getMessageConverters().set(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			this.template = template;
		}
		return this.template; 
	}
}
