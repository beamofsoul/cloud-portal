package com.moraydata.general.management.message;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Message code manager which controls all aspects of message code stuff.
 * @author Mingshu Jian  
 * @date 2018-03-30
 */
@ConditionalOnBean(MessageCodeSender.class)
@Component
public class MessageCodeManager {

	@Setter
	@Getter
	private String contentTemplate = "手机验证码%s，在五分钟内有效。";
	private int digits = 6; // 10 * 10 * 10 * 10 * 10 * 10
	@Setter
	@Getter
	private long time2Live = 5 * 60; // Seconds
	@Getter
	private TimeUnit timeUnit = TimeUnit.SECONDS;
	@Setter
	private MessageCodeSender messageCodeSender;
	
	public boolean send(String phone, int code, String content) throws Exception {
		if (messageCodeSender == null)
			messageCodeSender = new DefaultMessageCodeSender();
		return messageCodeSender.send(phone, code, content);
	}
	
	public boolean send(String phone, int code) throws Exception {
		return send(phone, code, contentTemplate);
	}
	
	public Integer send(String phone) throws Exception {
		int randomMessageCode = getRandomMessageCode();
		boolean send = send(phone, randomMessageCode, contentTemplate);
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
}
