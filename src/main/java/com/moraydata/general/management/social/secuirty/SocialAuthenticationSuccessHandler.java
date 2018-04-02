package com.moraydata.general.management.social.secuirty;

import static com.moraydata.general.management.util.HttpSessionUtils.isCurrentUserExist;
import static com.moraydata.general.management.util.HttpSessionUtils.saveCurrentUser;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.moraydata.general.management.security.AuthenticationSuccessHandler;
import com.moraydata.general.management.social.GlobalSocialConfiguration;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.dto.UserExtension;

/**
 * {@link org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler} extensions that customizes actions happen when onAuthenticationSuccess once logging user does not use remember-me functionality.
 * @author MingshuJian
 * @date 2018-03-28
 */
@ConditionalOnBean(GlobalSocialConfiguration.class)
@Component
public class SocialAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		if (!isCurrentUserExist(request.getSession())) {
			SocialUserExtension sue = (SocialUserExtension) authentication.getPrincipal();
			UserExtension userExtension = new UserExtension(
					Long.valueOf(sue.getUserId()), sue.getUsername(), sue.getPassword(), 
					sue.getNickname(), sue.getPhotoString(), sue.isEnabled(), 
					sue.isAccountNonExpired(), sue.isCredentialsNonExpired(), 
					sue.isAccountNonLocked(), sue.getAuthorities());
			
			saveCurrentUser(request.getSession(), userExtension);
			request.getSession().setAttribute(Constants.CURRENT.LOGIN, authenticationSuccessHandler.new LoginRecordHandler().saveLoginRecord(new User(userExtension.getUserId()), request, response));
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
