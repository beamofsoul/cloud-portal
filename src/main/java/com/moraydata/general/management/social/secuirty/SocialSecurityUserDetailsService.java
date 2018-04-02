package com.moraydata.general.management.social.secuirty;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

import com.moraydata.general.management.social.GlobalSocialConfiguration;
import com.moraydata.general.primary.entity.Role;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

@ConditionalOnBean({GlobalSocialConfiguration.class, UserService.class})
@Component
public class SocialSecurityUserDetailsService implements SocialUserDetailsService {
	
	@Autowired
	private UserService userService;

	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		User user = userService.get(Long.valueOf(userId)); // getUserById
		if (user != null) {
			SocialUserExtension socialUserExtension = new SocialUserExtension(
					user.getId(), user.getUsername(), user.getPassword(), user.getNickname(), user.getPhotoString(),
	                true, // if enable is true, otherwise false.
	                true, // if accountNonExpired is true, otherwise false. 
	                true, // if credentialsNonExpired is true, otherwise false.
	                true, // if accountNonLocked is true, otherwise false.
	                getAuthorities(user));
			return socialUserExtension;
		}
		return null;
	}

	protected static Set<GrantedAuthority> getAuthorities(User user) {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (Role role : user.getRoles())
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
		return authorities;
	}
}
