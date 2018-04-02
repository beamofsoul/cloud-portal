package com.moraydata.general.management.social.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * Configure filter of third party authorization
 * @author Mingshu Jian  
 * @date 2018年3月27日
 */
public class SocialFilterConfigurer extends SpringSocialConfigurer {
	
	@Autowired
	private AuthenticationSuccessHandler socialAuthenticationSuccessHandler;
	
	private String filterProcessesUrl;
	
	public SocialFilterConfigurer(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setAuthenticationSuccessHandler(socialAuthenticationSuccessHandler);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		return (T) filter;
	}
}
