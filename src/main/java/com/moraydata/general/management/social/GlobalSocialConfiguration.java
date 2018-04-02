package com.moraydata.general.management.social;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import com.moraydata.general.management.social.secuirty.SocialSecurityConnectionSignUp;
import com.moraydata.general.management.social.support.SocialFilterConfigurer;

/**
 * Social Configuration
 * @author Mingshu Jian
 * @date 2018-03-27 10:00
 */
@ConditionalOnProperty(name = "project.base.social.enabled", havingValue = "true")
@Configuration
@EnableSocial
@Order(10)
public class GlobalSocialConfiguration extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired(required = false)
	private ConnectionSignUp connectionSignUp;
	
	@Value("${project.base.social.repository.tablePrefix}")
	String tablePrefix;
	
	@Value("${project.base.social.filter.url}")
	String filterProcessesUrl;
	
	@Value("${project.base.social.filter.defaultFailureUrl}")
	String defaultFailureUrl;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		repository.setTablePrefix(tablePrefix);
		if (connectionSignUp != null) {
			repository.setConnectionSignUp(connectionSignUp);
		}
		return repository;
	}

	/**
	 * Set a strategy which determines how to get an account ID of the current user when using social components
	 * @author Mingshu Jian
	 * @date 2018-03-25 15:22:00
	 */
	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}
	
	/**
	 * Social SignIn Configuration
	 * Designed for third party sign-in system which follows the OpenID OAuth, such as WeChat.
	 */
	@Bean
	public SpringSocialConfigurer socialFilterConfigurer() {
		return new SocialFilterConfigurer(filterProcessesUrl).defaultFailureUrl(defaultFailureUrl);
	}

	/**
	 * Instance an util class which is able to provide supportive methods when processing sign-in 
	 */
	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		return new ProviderSignInUtils(connectionFactoryLocator, getUsersConnectionRepository(connectionFactoryLocator)) {};
	}
	
	/**
	 * Automatic sign-up under third party authorization
	 */
	@ConditionalOnBean(PasswordEncoder.class)
	@Bean
	public ConnectionSignUp SocialSecurityConnectionSignUp() {
		return new SocialSecurityConnectionSignUp();
	}
}
