package com.moraydata.general.management.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.social.security.SpringSocialConfigurer;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.moraydata.general.primary.service.LoginService;
import com.moraydata.general.primary.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityConfigurationProperties props;
	
	@Autowired
	private SecurityPermissionEvaluator securityPermissionEvaluator;
	
	@Autowired
	private AccessDeniedHandler defaultAccessDeniedHandler;
	
	@Autowired
//	private org.thymeleaf.templateresolver.TemplateResolver tmeplateResolver;
	private ITemplateResolver templateResolver;
	
	@Autowired(required = false)
	private SpringSocialConfigurer socialFilterConfigurer;
	
	@Bean
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@ConditionalOnBean(LoginService.class)
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new AuthenticationSuccessHandler();
	}
	
	@Bean
	@ConditionalOnBean(UserService.class)
	public AuthenticationUserDetailsService authenticationUserDetailsService() {
		return new AuthenticationUserDetailsService();
	}
	
	@Bean
	@ConditionalOnBean({AuthenticationUserDetailsService.class, PasswordEncoder.class})
	public SecretKeyAuthenticationProvider secretKeyAuthenticationProvider() {
		return new SecretKeyAuthenticationProvider();
	}
	
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new SpringSecurityDialect());
		return templateEngine;
	}
	
	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new RememberMeServices(props.getRememberMeCookieName(), authenticationUserDetailsService());
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers(props.getAdminRoleMatchers()).hasAnyRole(props.getAdminRoles())
				.antMatchers(props.getNonAuthenticatedMatchers()).permitAll()
				.and()
			.formLogin()
				.loginPage(props.getLoginPage())
				.permitAll()
				.defaultSuccessUrl(props.getDefaultLoginSuccessUrl(), props.isAlwaysUseDefaultSuccessUrl())
				.successHandler(authenticationSuccessHandler())
				.and()
				.logout()
				.logoutUrl(props.getLogoutUrl())
				.logoutSuccessUrl(props.getDefaultLogoutSuccessUrl())
				.and()
			.sessionManagement()
				.maximumSessions(props.getMaximumSessions())
				.maxSessionsPreventsLogin(props.isMaxSessionsPreventsLogin())
				.expiredUrl(props.getExpiredUrl())
				.and()
				.and()
			.rememberMe()
				.tokenValiditySeconds(props.getTokenValiditySeconds())
				.rememberMeParameter(props.getRememberMeParameter())
				.rememberMeServices(rememberMeServices())
				.and()
			.exceptionHandling()
				.accessDeniedHandler(defaultAccessDeniedHandler); // response as 403 when access denied by Ajax

		/**
		 * Do not apply security configuration of social component when social component is not exist
		 */
		if (socialFilterConfigurer != null) {
			http.apply(socialFilterConfigurer); // filter for WeChat login
		}
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(props.getIgnoringMatchers());
		DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
		handler.setPermissionEvaluator(securityPermissionEvaluator);
		web.expressionHandler(handler);
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(secretKeyAuthenticationProvider());
	}
}
