package com.moraydata.general.management.message;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Message code manager which controls all aspects of message code stuff.
 * @author Mingshu Jian  
 * @date 2018-03-30
 */
@ConditionalOnProperty(name = "project.base.message.enabled", havingValue = "true")
@Component
public class MessageCodeManager {

	private int digits = 6; // 10 * 10 * 10 * 10 * 10 * 10
	@Setter
	@Getter
	private long time2Live = 5 * 60; // Seconds
	@Getter
	private TimeUnit timeUnit = TimeUnit.SECONDS;
	@Setter
	@Autowired
	private MessageCodeSender messageCodeSender;
	
	@SuppressWarnings("serial")
	public boolean send(String phone, int code) throws Exception {
		return getMessageCodeSender().send(phone, new HashMap<String, Object>(){{
			put("code", code);
		}});
	}
	
	public Integer send(String phone) throws Exception {
		int randomMessageCode = getRandomMessageCode();
		boolean send = send(phone, randomMessageCode);
		return send ? randomMessageCode : null;
	}

	public long getExpiredDate(long currentClientMilliseconds) {
		return getExpiredDate(currentClientMilliseconds, time2Live);
	}
	
	public long getExpiredDate(long currentClientMilliseconds, long time2Live) {
		return currentClientMilliseconds + time2Live * 1000;
	}
	
	public int getRandomMessageCode(int digits) {
		return RandomUtils.nextInt(new Double(Math.pow(10, digits - 1)).intValue(), new Double(Math.pow(10, digits)).intValue());
	}
	
	public int getRandomMessageCode() {
		return getRandomMessageCode(digits);
	}

	public MessageCodeSender getMessageCodeSender() {
//		if (messageCodeSender == null)
//			messageCodeSender = new AliyunVerificationCodeSender();
		return messageCodeSender;
	}
}
