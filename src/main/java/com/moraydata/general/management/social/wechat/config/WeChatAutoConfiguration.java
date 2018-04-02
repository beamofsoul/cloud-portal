package com.moraydata.general.management.social.wechat.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;

import com.moraydata.general.management.social.GlobalSocialConfiguration;
import com.moraydata.general.management.social.wechat.connect.WeChatConnectionFactory;

/**
 * WeChat Sign-in Configuration  
 * @author MingshuJian  
 * @date 2018-03-27
 */
@ConditionalOnBean({GlobalSocialConfiguration.class, DataSource.class})
@Configuration
public class WeChatAutoConfiguration extends SocialConfigurerAdapter {
	
	@Value("${project.base.social.provider}")
	String provider;
	
	@Value("${project.base.social.client.appId}")
	String appId;
	
	@Value("${project.base.social.client.appSecret}")
	String appSecret;

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer configurer, Environment environment) {
		configurer.addConnectionFactory(new WeChatConnectionFactory(provider, appId, appSecret));
	}
}
