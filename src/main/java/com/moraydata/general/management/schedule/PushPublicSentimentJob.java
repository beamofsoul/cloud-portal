package com.moraydata.general.management.schedule;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moraydata.general.management.util.CollectionUtils;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.HttpUrlUtils;
import com.moraydata.general.management.util.ObjectMapperUtils;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.management.util.RestTemplateUtils;
import com.moraydata.general.management.util.WeChatTokenHelper;
import com.moraydata.general.primary.entity.dto.UserBasicInformation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PushPublicSentimentJob extends QuartzJobBean {
	
	@Value("${project.base.wechat.service.sendTemplateMessageUrl}")
	private String sendTemplateMessageUrl;
	
	@Value("${project.base.wechat.service.templateMessage.publicSentiment}")
	private String publicSentimentTemplateId;
	
	@Value("${project.base.business.publicSentiment.interfaceUrl}")
	private String publicSentimentInterfaceUrl;
	
	@Autowired
	private WeChatTokenHelper handler;
	
	/**
	 * 每3分钟从舆情系统获取未发送的模板消息记录和目标用户，定时向目标用户发送模板消息
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			// 1. 获取所有用户和此3分钟消息集合的映射
			Map<String, List<LinkedHashMap>> userTemplateMessagesMap = getTemplateMessages();
			if (userTemplateMessagesMap == null) {
				log.error("获取未发送的舆情推送消息失败!");
				return;
			}
			for (Map.Entry<String, List<LinkedHashMap>> each : userTemplateMessagesMap.entrySet()) {
				List<LinkedHashMap> messages = each.getValue();
				
				if (CollectionUtils.isNotBlank(messages)) {
					String openId = each.getKey();
					// 2. 遍历发送所有消息给当前用户
					for (LinkedHashMap value : messages) {
						doSend(openId, mapper.convertValue(value, TemplateMessage.class));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用发送模板消息方法，对结果返回值进行判断，无论发送成功或失败，都将记录到日志中
	 * @param openId 要发送到那个微信用户所对应的openId
	 * @param templateMessage 模板消息对象，包含了模板消息的内容
	 */
	private void doSend(String openId, TemplateMessage templateMessage) throws Exception {
		Map<String, Map<String, String>> content = fillAndGetDataMap(templateMessage);
		String redirectUrl = templateMessage.getRedirectUrl();
		boolean sent = sendTemplateMessage(openId, publicSentimentTemplateId, redirectUrl, content);
		String jsonContent = JSON.toJSONString(content);
		if (!sent) {
			log.error(String.format("一个模板消息发送失败! 目标用户OpenId: %s, 所发模板Id: %s, 目的地址: %s, 模板内容: %s", openId, publicSentimentTemplateId, redirectUrl, jsonContent));
		} else {
			log.info(String.format("一个模板消息已经发送成功! 目标用户OpenId: %s, 所发模板Id: %s, 目的地址: %s, 模板内容: %s", openId, publicSentimentTemplateId, redirectUrl, jsonContent));
		}
	}
	
	/**
	 * 通过输入的用户基本信息对象集合和(舆情异常提醒)模板消息Id，向特定用户发送改模板消息Id所对应的模板消息
	 * @param users 用户基本信息对象集合，模板消息发送的目标用户集合
	 * @param templateMessageId 要发送的模板消息对应的Id
	 */
	public void sendTemplateMessage(List<UserBasicInformation> users, Long templateMessageId) throws Exception {
		if (users == null || users.isEmpty()) return; // 如果不能获得发送到哪些用户，则谈不上发送模板消息的问题，发送自动失败
		TemplateMessage tm = getTemplateMessage(templateMessageId);
		for (UserBasicInformation ubi : users)
			doSend(ubi.getOpenId(), tm);
	}
	
	/**
	 * 通过模板消息记录主键Id获取对应的模板消息记录对象
	 * @param templateMessageId 模板消息Id
	 * @return TemplateMessage 获取到的模板消息记录对象
	 */
	public TemplateMessage getTemplateMessage(Long templateMessageId) throws Exception {
		Map<?, ?> response = RestTemplateUtils.INSTANCE.getRestTemplate().getForObject(publicSentimentInterfaceUrl + templateMessageId, Map.class);
		ResponseEntity responseEntity = ObjectMapperUtils.INSTANCE.convert(response, ResponseEntity.class);
		if (responseEntity.isSuccessful()) {
			return JSON.parseObject(responseEntity.getData().toString(), TemplateMessage.class);
		} else {
			log.error(responseEntity.getMessage());
			return null; 
		}
	}

	/**
	 * 通过模板消息记录Id和目标行为状态，修改对应的模板消息的状态值，如从未处理改为已上报
	 * @param templateMessageId 模板消息Id
	 * @param action 修改后的模板消息的状态
	 */
	@SuppressWarnings("serial")
	public void updateAction(Long templateMessageId, Integer action) throws Exception {
		String url = HttpUrlUtils.integrate(publicSentimentInterfaceUrl + "modifyStatus", new HashMap<String, Object>() {{
			put("templateMessageId", templateMessageId);
			put("action", action);
		}});
		RestTemplateUtils.INSTANCE.getRestTemplate().exchange(url, HttpMethod.PUT, RestTemplateUtils.getHttpEntity(), JSONObject.class);
	}
	
	/**
	 * 从舆情系统获取未发送过的模板消息记录，该方法在定时器中使用
	 * @return Map<String,List<LinkedHashMap>> key为要发送至目标用户的openId，value为针对此目标用户需要发送的模板消息记录列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, List<LinkedHashMap>> getTemplateMessages() {
		Map response = RestTemplateUtils.INSTANCE.getRestTemplate().getForObject(publicSentimentInterfaceUrl + "queryNotpushed", Map.class);
		ResponseEntity responseEntity = ObjectMapperUtils.INSTANCE.convert(response, ResponseEntity.class);
		if (responseEntity.isSuccessful()) {
			return JSON.parseObject(responseEntity.getData().toString(), Map.class);
		} else {
			log.error(responseEntity.getMessage());
			return null; 
		}
	}
	
	/**
	 * 通过输入的模板消息实例，填充好一个可以被传入restTemplate作为参数结构的Map对象，将被用作为发送模板消息的输入参数
	 * @param templateMessage 模板消息记录实例
	 * @return Map<String,Map<String,String>> 填充好的Map对象，分别对应微信模板消息的关键字和值，比如first、keyword1、remark等
	 */
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
	
	/**
	 * 通过输入的微信服务号目标用户openId、微信模板消息模板Id、点击模板消息要跳转到的url、具体的模板消息内容来发送模板消息至目标用户，无论发送成功或失败都将会发送结果打印到日志中
	 * @param openId 微信服务号目标用户openId
	 * @param templateId 微信模板消息模板Id
	 * @param targetUrl 点击模板消息要跳转到的url
	 * @param data 具体的模板消息内容
	 * @return boolean 是否发送成功
	 */
	public boolean sendTemplateMessage(String openId, String templateId, String targetUrl, Map<String, Map<String, String>> data) throws Exception {
		if (StringUtils.isBlank(openId)) return false; // 如果目标用户的openId不合法，则发送必然会失败
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("touser", openId); // 接收者openId
		parameters.put("template_id", templateId); // 模板Id
		parameters.put("url", targetUrl + "?openId=" + openId);
		parameters.put("data", data);
		
		JSONObject jsonResponse = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integrateUrlWithAccessToken(sendTemplateMessageUrl), RestTemplateUtils.getHttpEntity(parameters), JSONObject.class);
		log.debug(JSON.toJSONString(jsonResponse));
		return jsonResponse.getString("errmsg").equals("ok");
	}
	
	/**
	 * 根据输入的url在其后拼接微信公众号access_token参数和值
	 * @param baseUrl 未加参数的url 
	 * @return String 已经在输入url后增加了access_token参数和值的完整url
	 */
	private String integrateUrlWithAccessToken(final String baseUrl) {
		return integrateUrlWithAccessToken(baseUrl, handler.getToken().getAccessToken());
	}
	
	/**
	 * 根据输入的url在其后拼接微信公众号access_token参数和值
	 * @param baseUrl 未加参数的url
	 * @param accessToken 微信服务号access_token的值 
	 * @return String 已经在输入url后增加了access_token参数和值的完整url
	 */
	@SuppressWarnings("serial")
	private String integrateUrlWithAccessToken(final String baseUrl, String accessToken) {
		String integratedUrl = HttpUrlUtils.integrate(baseUrl, new HashMap<String, Object>() {{
        	put(Constants.WECHAT.ACCESS_TOKEN, accessToken);
        }});
		return integratedUrl;
	}
}
