package com.moraydata.general.management.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.moraydata.general.primary.entity.dto.UserExtension;

/**
 * @ClassName SecretKeyAuthenticationProvider
 * @Description {@link org.springframework.security.authentication.AuthenticationProvider} implementation that determines whether the secret key (include be not limited to password) is correct when logging. 
 * @author MingshuJian
 * @Date 2017年1月19日 下午4:18:21
 * @version 1.0.0
 */
public class SecretKeyAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private AuthenticationUserDetailsService authenticationUserDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	/*
	 * (非 Javadoc)  
	 * <p>Title: authenticate</p>  
	 * <p>Description: According to the username and password typed, to determine whether:
	 * <p>  1. Username has been inputed.</p>
	 * <p>  2. There is an user record which has a same username in DB.</p>
	 * <p>  3. The password inputed is same as the password of the user record from DB.</p>
	 * <p>  4. Logging user is not locked.</p>
	 * <p>  5. Logging user is not expired.</p>
	 * <p>  6. Logging user has at least one role.</p>
	 * @param authentication
	 * @return UsernamePasswordAuthenticationToken
	 * @throws AuthenticationException  
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final UserDetails user = authenticationUserDetailsService.loadUserByUsername(authentication.getName());
		
		// whether login by remember-me functionality
		final UsernameNotFoundException wrongPasswordException = new UsernameNotFoundException("Password is wrong");
		if (authentication.getPrincipal() instanceof UserExtension) {
			if (!passwordEncoder.matches(((UserExtension) authentication.getPrincipal()).getPassword(), user.getPassword()))
				throw wrongPasswordException;
		} else if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
			throw wrongPasswordException;
		}

		UsernamePasswordAuthenticationToken result = 
				new UsernamePasswordAuthenticationToken(
				user.getUsername(), 
				user.getPassword(),
				user.getAuthorities());
		result.setDetails(user);
		return result;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}
}
