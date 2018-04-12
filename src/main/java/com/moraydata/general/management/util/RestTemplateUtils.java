package com.moraydata.general.management.util;

import java.nio.charset.StandardCharsets;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public enum RestTemplateUtils {

	INSTANCE;
	
	private RestTemplate restTemplate;
	
	private RestTemplateUtils() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().set(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		this.setRestTemplate(template);
	}
	
	public RestTemplate getRestTemplate() {
		return this.restTemplate;
	}
	
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
}
