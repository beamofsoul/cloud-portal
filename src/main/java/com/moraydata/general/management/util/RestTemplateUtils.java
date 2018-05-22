package com.moraydata.general.management.util;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public enum RestTemplateUtils {

	INSTANCE;
	
	private RestTemplate restTemplate;
	
	@Value("${project.base.rest.connection.connectionTimeout}")
	private Integer connectionTimeout;
	
	@Value("${project.base.rest.connection.connectionTimeout}")
	private Integer readTimeout;
	
	private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
	private static final int DEFAULT_READ_TIMEOUT = 5000;
	
	private RestTemplateUtils() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout((connectionTimeout == null || connectionTimeout.intValue() == 0) ? DEFAULT_CONNECTION_TIMEOUT : connectionTimeout.intValue());
		requestFactory.setReadTimeout((readTimeout == null || readTimeout.intValue() == 0) ? DEFAULT_READ_TIMEOUT : readTimeout.intValue());
		RestTemplate template = new RestTemplate(requestFactory);
		template.getMessageConverters().set(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		this.setRestTemplate(template);
	}
	
	public RestTemplate getRestTemplate() {
		return this.restTemplate;
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static HttpEntity getHttpEntity() {
		return getHttpEntity(null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HttpEntity getHttpEntity(Object parameters) {
		return new HttpEntity(parameters, new HttpHeaders());
	}
}
