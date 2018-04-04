package com.moraydata.general.management.message;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AliyunVerificationCodeSender implements MessageCodeSender {

	// 产品名称:云通信短信API产品,开发者无需替换  
	@Value("${project.base.message.product}")
    private String product;  
    // 产品域名,开发者无需替换  
	@Value("${project.base.message.domain}")
    private String domain;  

    @Value("${project.base.message.accessKeyId}")
    private String accessKeyId;
    
    @Value("${project.base.message.accessKeySecret}")
    private String accessKeySecret;
    
    @Value("${project.base.message.sendTimeout}")
    private String sendTimeout;
    
    @Value("${project.base.message.signName}")
    private String signName;
    
    @Value("${project.base.message.verificationCodeTemplateCode}")
    private String verificationCodeTemplateCode;
    
	@Override
	public boolean send(String phone, Map<String, Object> params) throws ClientException {
		if (StringUtils.isBlank(phone)) {
			log.debug("Given phone number(s) must not be null for sending verification codes");
			return false;
		} else if (params == null || params.isEmpty()) {
			log.debug("Given params must not be null or empty for sending verification codes");
			return false;
		}
		
		initializeTimeoutProperties(sendTimeout);
		IAcsClient acsClient = initializeAcsClient(product, domain, accessKeyId, accessKeySecret);

		SendSmsRequest request = new SendSmsRequest();
		request.setPhoneNumbers(phone);
		request.setSignName(signName);
		request.setTemplateCode(verificationCodeTemplateCode);
		request.setTemplateParam(JSON.toJSONString(params));
		
		return doSend(acsClient, request);
	}

	boolean doSend(IAcsClient acsClient, SendSmsRequest request) throws ServerException, ClientException {
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		boolean sent = sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK");
		log.info(String.format("The response of sending varification codes is %s", JSON.toJSONString(sendSmsResponse)));
		return sent;
	}

	// 可自助调整超时时间
	void initializeTimeoutProperties(String sendTimeout) {
		System.setProperty("sun.net.client.defaultConnectTimeout", sendTimeout);
		System.setProperty("sun.net.client.defaultReadTimeout", sendTimeout);
	}

	// 初始化acsClient,暂不支持region化
	IAcsClient initializeAcsClient(String product, String domain, String accessKeyId, String accessKeySecret) throws ClientException {
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		return new DefaultAcsClient(profile);
	}
}
