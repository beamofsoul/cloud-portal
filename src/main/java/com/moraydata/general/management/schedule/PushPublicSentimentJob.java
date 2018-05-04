package com.moraydata.general.management.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moraydata.general.management.util.CollectionUtils;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.HttpUrlUtils;
import com.moraydata.general.management.util.RestTemplateUtils;
import com.moraydata.general.management.util.WeChatTokenHelper;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PushPublicSentimentJob extends QuartzJobBean {
	
	@Value("${project.base.wechat.service.sendTemplateMessageUrl}")
	private String sendTemplateMessageUrl;
	
//	private static final String SEND_TEMPLATE_MESSAGE_PATH = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	
	public static final String TEMPLATE_ID = "259crn-Levvu-2VDe2jff43bHncbzyQC3LWCTpZCWBw";
	
	@Autowired
	private WeChatTokenHelper handler;
	
//	@Autowired
//	private UserService userService;

	@SuppressWarnings("rawtypes")
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
//			// 1. 获取所有有openId的用户
////			List<User> userList = userService.getWhoHasOpenId();
//			List<User> userList = Arrays.asList(userService.getByOpenId("oQFmP0-K4IvdQvocmDHiJ5aPn9Uk"));
//			
//			// 2. 获取所有用户和此3分钟消息集合的映射
//			Map<Long, List<TemplateMessage>> userTemplateMessages = getTemplateMessages();
//			
//			for (User user : userList) {
//				String openId = user.getOpenId();
//				Long userId = user.getId();
//				
//				List<TemplateMessage> currentMessages = userTemplateMessages.get(userId);
//				if (currentMessages == null || currentMessages.isEmpty()) {
//					continue;
//				}
//				
//				// 3.遍历发送所有消息给当前用户
//				for (TemplateMessage templateMessage : currentMessages) {
//					sendTemplateMessage(openId, TEMPLATE_ID, "www.baidu.com", fillAndGetDataMap(templateMessage));
//				}
//			}
			
			ObjectMapper mapper = new ObjectMapper();
			
			// 1. 获取所有用户和此3分钟消息集合的映射
			Map<String, List<LinkedHashMap>> userTemplateMessagesMap = getTemplateMessages();
			for (Map.Entry<String, List<LinkedHashMap>> each : userTemplateMessagesMap.entrySet()) {
				List<LinkedHashMap> messages = each.getValue();
				
				if (CollectionUtils.isNotBlank(messages)) {
					String openId = each.getKey();
					// 2. 遍历发送所有消息给当前用户
					for (LinkedHashMap value : messages) {
						TemplateMessage templateMessage = mapper.convertValue(value, TemplateMessage.class);
						int randomInt = (int)(1+Math.random()*(1000-1+1));
						templateMessage.setRemarkValue(templateMessage.getRemarkValue() + "_" + randomInt);
						Map<String, Map<String, String>> content = fillAndGetDataMap(templateMessage);
						String redirectUrl = templateMessage.getRedirectUrl();
						boolean sent = sendTemplateMessage(openId, TEMPLATE_ID, redirectUrl, content);
						String jsonContent = JSON.toJSONString(content);
						if (!sent) {
							log.error(String.format("一个模板消息发送失败![%s] 目标用户OpenId: %s, 所发模板Id: %s, 目的地址: %s, 模板内容: %s", randomInt, openId, TEMPLATE_ID, redirectUrl, jsonContent));
						} else {
							log.info(String.format("一个模板消息已经发送成功![%s] 目标用户OpenId: %s, 所发模板Id: %s, 目的地址: %s, 模板内容: %s", randomInt, openId, TEMPLATE_ID, redirectUrl, jsonContent));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendTemplateMessage(List<UserBasicInformation> users, Long templateMessageId) {
		if (users == null) {
			return;
		}
		
		// 1. 获取TemplateMessage
		TemplateMessage tm = getTemplateMessage(templateMessageId);
		
		// 2. 将消息发送给所有用户
		for (UserBasicInformation ubi : users) {
			int randomInt = (int)(1+Math.random()*(1000-1+1));
			String openId = ubi.getOpenId();
			String redirectUrl = tm.getRedirectUrl();
			Map<String, Map<String, String>> content = fillAndGetDataMap(tm);
			boolean sent = sendTemplateMessage(openId, TEMPLATE_ID, redirectUrl, content);
			String jsonContent = JSON.toJSONString(content);
			if (!sent) {
				log.error(String.format("一个模板消息发送失败![%s] 目标用户OpenId: %s, 所发模板Id: %s, 目的地址: %s, 模板内容: %s", randomInt, openId, TEMPLATE_ID, redirectUrl, jsonContent));
			} else {
				log.info(String.format("一个模板消息已经发送成功![%s] 目标用户OpenId: %s, 所发模板Id: %s, 目的地址: %s, 模板内容: %s", randomInt, openId, TEMPLATE_ID, redirectUrl, jsonContent));
			}
		}
	}
	
	private TemplateMessage getTemplateMessage(Long templateMessageId) {
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, List<LinkedHashMap>> getTemplateMessages() {
		return RestTemplateUtils.INSTANCE.getRestTemplate().getForObject("http://192.168.31.26/wxNotice/queryNotpushed", Map.class);
	}
	
	@SuppressWarnings("serial")
	public Map<String, Map<String, String>> fillAndGetDataMap(TemplateMessage templateMessage) {
		final String value = Constants.TEMPLATE_MESSAGE.VALUE;
		final String color = Constants.TEMPLATE_MESSAGE.COLOR;
		return new HashMap<String, Map<String, String>>() {{
			put(Constants.TEMPLATE_MESSAGE.FIRST, new HashMap<String, String>() {{
				put(value, templateMessage.getFirstValue());
				put(color, templateMessage.getFirstColor());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD1, new HashMap<String, String>() {{
				put(value, templateMessage.getKeyword1Value());
				put(color, templateMessage.getKeyword1Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD2, new HashMap<String, String>() {{
				put(value, templateMessage.getKeyword2Value());
				put(color, templateMessage.getKeyword2Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD3, new HashMap<String, String>() {{
				put(value, templateMessage.getKeyword3Value());
				put(color, templateMessage.getKeyword3Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD4, new HashMap<String, String>() {{
				put(value, templateMessage.getKeyword4Value());
				put(color, templateMessage.getKeyword4Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.REMARK, new HashMap<String, String>() {{
				put(value, templateMessage.getRemarkValue());
				put(color, templateMessage.getRemarkColor());
			}});
		}};
	}
	
	public boolean sendTemplateMessage(String openId, String templateId, String targetUrl, Map<String, Map<String, String>> data) {
		if (StringUtils.isBlank(openId)) {
			return false;
		}
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("touser", openId); // 接收者openId
		parameters.put("template_id", templateId); // 模板Id
		parameters.put("url", targetUrl + "?openId=" + openId);
		parameters.put("data", data);
		
		JSONObject jsonResponse = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integrateUrlWithAccessToken(sendTemplateMessageUrl), RestTemplateUtils.getHttpEntity(parameters), JSONObject.class);
		log.debug(JSON.toJSONString(jsonResponse));
		return jsonResponse.getString("errmsg").equals("ok");
	}
	
	private String integrateUrlWithAccessToken(final String baseUrl) {
		return integrateUrlWithAccessToken(baseUrl, handler.getToken().getAccessToken());
	}
	
	@SuppressWarnings("serial")
	private String integrateUrlWithAccessToken(final String baseUrl, String accessToken) {
		String integratedUrl = HttpUrlUtils.integrate(baseUrl, new HashMap<String, Object>() {{
        	put(Constants.WECHAT.ACCESS_TOKEN, accessToken);
        }});
		return integratedUrl;
	}
}
