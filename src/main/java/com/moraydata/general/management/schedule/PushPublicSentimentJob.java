package com.moraydata.general.management.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.HttpUrlUtils;
import com.moraydata.general.management.util.RestTemplateUtils;
import com.moraydata.general.management.util.WeChatTokenHelper;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PushPublicSentimentJob extends QuartzJobBean {
	
	private static final String COLOR = "color";

	private static final String VALUE = "value";

	private static final String SEND_TEMPLATE_MESSAGE_PATH = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	
	private static final String TEMPLATE_ID = "259crn-Levvu-2VDe2jff43bHncbzyQC3LWCTpZCWBw";
	
	@Autowired
	private WeChatTokenHelper handler;
	
	@Autowired
	private UserService userService;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("touser", "oQFmP09rAj4o3lPUblbgZMfp0T24"); // 接收者openId
//		parameters.put("template_id", "259crn-Levvu-2VDe2jff43bHncbzyQC3LWCTpZCWBw"); // 模板Id
//		parameters.put("url", "http://www.baidu.com");
//		parameters.put("data", new HashMap<String, Map<String, String>>() {{
//			put("first", new HashMap<String, String>() {{
//				put("value", "海鳗云平台监测到一个异常事件");
//				put("color", "#173177");
//			}});
//			put("keyword1", new HashMap<String, String>() {{
//				put("value", "自然灾害");
//				put("color", "#173177");
//			}});
//			put("keyword2", new HashMap<String, String>() {{
//				put("value", "九寨沟");
//				put("color", "#173177");
//			}});
//			put("keyword3", new HashMap<String, String>() {{
//				put("value", "2018-01-01");
//				put("color", "#173177");
//			}});
//			put("keyword4", new HashMap<String, String>() {{
//				put("value", "阿坝；九寨沟；地震；8级");
//				put("color", "#173177");
//			}});
//			put("remark", new HashMap<String, String>() {{
//				put("value", "请及时查看处置!");
//				put("color", "#173177");
//			}});
//		}});
		
		
		try {
			// 1. 获取所有有openId的用户
//			List<User> userList = userService.getWhoHasOpenId();
			List<User> userList = Arrays.asList(userService.getByOpenId("oQFmP0-K4IvdQvocmDHiJ5aPn9Uk"));
			
			// 2. 获取所有用户和此3分钟消息集合的映射
			Map<Long, List<TemplateMessage>> userTemplateMessages = getTemplateMessages();
			
			for (User user : userList) {
				String openId = user.getOpenId();
				Long userId = user.getId();
				
				List<TemplateMessage> currentMessages = userTemplateMessages.get(userId);
				if (currentMessages == null || currentMessages.isEmpty()) {
					continue;
				}
				
				// 3.遍历发送所有消息给当前用户
				for (TemplateMessage templateMessage : currentMessages) {
					sendTemplateMessage(openId, TEMPLATE_ID, "www.baidu.com", fillAndGetDataMap(templateMessage));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("serial")
	private Map<Long, List<TemplateMessage>> getTemplateMessages() {
		// RestTemplate
//		return null;
		
		return new HashMap<Long, List<TemplateMessage>>() {{
			put(53L, new ArrayList<TemplateMessage>() {{
				add(new TemplateMessage(
						"海鳗云平台监测到一个异常事件", "#173177", 
						"自然灾害", "#173177", 
						"九寨沟", "#173177",
						"2018-01-01", "#173177",
						"阿坝;九寨沟;地震;8级;", "#173177",
						"请及时查看处置!", "#173177"
				));
			}});
		}};
	}
	
	@SuppressWarnings("serial")
	private Map<String, Map<String, String>> fillAndGetDataMap(TemplateMessage templateMessage) {
		return new HashMap<String, Map<String, String>>() {{
			put(Constants.TEMPLATE_MESSAGE.FIRST, new HashMap<String, String>() {{
				put(VALUE, templateMessage.getFirstValue());
				put(COLOR, templateMessage.getFirstValue());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD1, new HashMap<String, String>() {{
				put(VALUE, templateMessage.getKeyword1Value());
				put(COLOR, templateMessage.getKeyword1Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD2, new HashMap<String, String>() {{
				put(VALUE, templateMessage.getKeyword2Value());
				put(COLOR, templateMessage.getKeyword2Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD3, new HashMap<String, String>() {{
				put(VALUE, templateMessage.getKeyword3Value());
				put(COLOR, templateMessage.getKeyword3Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.KEYWORD4, new HashMap<String, String>() {{
				put(VALUE, templateMessage.getKeyword4Value());
				put(COLOR, templateMessage.getKeyword4Color());
			}});
			put(Constants.TEMPLATE_MESSAGE.REMARK, new HashMap<String, String>() {{
				put(VALUE, templateMessage.getRemarkValue());
				put(COLOR, templateMessage.getRemarkColor());
			}});
		}};
	}
	
	private boolean sendTemplateMessage(String openId, String templateId, String targetUrl, Map<String, Map<String, String>> data) {
		if (StringUtils.isBlank(openId)) {
			return false;
		}
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("touser", openId); // 接收者openId
		parameters.put("template_id", templateId); // 模板Id
		parameters.put("url", targetUrl + "?openId=" + openId);
		parameters.put("data", data);
		
		JSONObject jsonResponse = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integrateUrlWithAccessToken(SEND_TEMPLATE_MESSAGE_PATH), RestTemplateUtils.getHttpEntity(parameters), JSONObject.class);
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
