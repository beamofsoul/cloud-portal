package com.moraydata.general.management.social.secuirty;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUser;

import lombok.Getter;
import lombok.Setter;

@Setter
public class SocialUserExtension extends SocialUser {

	private static final long serialVersionUID = 1L;
	
	private Long userId;
	
	@Getter
	private String nickname;
	
	@Getter
	private String password;
	
	@Getter
	private String photoString;
	
	@Getter
	private String sceneId; // 2018-04-10 When user using Wechat scanning QR code to login the system, open id is its' user-name and scene id is its' password. The default scene id for scanning QR code to login is 911

	public SocialUserExtension(Long userId, String username, String password, String nickname, String photoString, String sceneId, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.userId = userId;
		this.nickname = nickname;
		this.password = password;
		this.photoString = photoString;
		this.sceneId = sceneId;
	}
	
	@Override
	public String getUserId() {
		return String.valueOf(userId.longValue());
	}
}