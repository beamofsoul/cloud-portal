package com.moraydata.general.management.social.wechat.connect;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.moraydata.general.management.social.support.Constants;
import com.moraydata.general.management.util.HttpUrlUtils;

/**
 * Template to handle WeChat OAuth2 authentication process.
 * The way of implementing OAuth2 of domestic companies are quite different.
 * So that the default OAuth2Template provided by Spring cannot adapt to the specific process of each company.
 * Here we adjust the details of the process for WeChat.
 * @author Mingshu Jian
 * @date 2018-03-28 09:44
 */
public class WeChatOAuth2Template extends OAuth2Template {

	private String appId;

	private String appSecret;

	private String accessTokenUrl;
	
	public WeChatOAuth2Template(String appId, String appSecret, String authorizeUrl, String accessTokenUrl) {
		super(appId, appSecret, authorizeUrl, accessTokenUrl);
		setUseParametersForClientAuthentication(true);
		this.appId = appId;
		this.appSecret = appSecret;
		this.accessTokenUrl = accessTokenUrl;
	}
	
	@SuppressWarnings("serial")
	@Override
	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> parameters) {
		return getForAccessGrant(HttpUrlUtils.integrate(accessTokenUrl, new HashMap<String, Object>() {{
			put("appid", appId);
			put("secret", appSecret);
			put("code", authorizationCode);
			put("grant_type", "authorization_code");
			put("redirect_uri", redirectUri);
		}})) ;  
	}

	@SuppressWarnings("serial")
	@Override
	public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
		return getForAccessGrant(HttpUrlUtils.integrate(Constants.WeChat.REFRESH_TOKEN_URL, new HashMap<String, Object>() {{
			put("appid", appId);
			put("grant_type", "refresh_token");
			put("refresh_token", refreshToken);
		}}));
	}
	
	/**
	 * Gets the request for an access grant to the provider.
	 * The default implementation uses RestTemplate to request the access token and expects a JSON response to be bound to a Map. The information in the Map will be used to create an {@link AccessGrant}.
	 * Since the OAuth 2 specification indicates that an access token response should be in JSON format, there's often no need to override this method.
	 * If all you need to do is capture provider-specific data in the response, you should override createAccessGrant() instead.
	 * However, in the event of a provider whose access token response is non-JSON, you may need to override this method to request that the response be bound to something other than a Map.
	 * For example, if the access token response is given as form-encoded, this method should be overridden to call RestTemplate.postForObject() asking for the response to be bound to a MultiValueMap (whose contents can then be used to create an AccessGrant).
	 * @param accessTokenUrl the URL of the provider's access token endpoint.
	 * @param parameters the parameters to post to the access token endpoint.
	 * @return the access grant.
	 */
	@SuppressWarnings("unchecked")
	protected AccessGrant getForAccessGrant(String accessTokenUrl) {
		Map<String, Object> map = getRestTemplate().getForObject(accessTokenUrl, Map.class);
		
		if (StringUtils.isNotBlank(MapUtils.getString(map, "errcode"))) {
			String errcode = MapUtils.getString(map, "errcode");
			String errmsg = MapUtils.getString(map, "errmsg");
			throw new RuntimeException(String.format("Obtain access token failed, errcode: %s, errmsg: %s", errcode, errmsg));
		}
		
		return new WeChatAccessGrant(
				MapUtils.getString(map, "access_token"),
				MapUtils.getString(map, "scope"),
				MapUtils.getString(map, "refresh_token"),
				MapUtils.getLong(map, "expires_in"),
				MapUtils.getString(map, "openid"));
	}

	/**
	 * Build a request of getting authorized code.
	 * It will guide user and redirect to WeChat sign-in URL.
	 */
	@Override
	public String buildAuthenticateUrl(OAuth2Parameters parameters) {
		parameters.add("appid", appId);
		parameters.add("scope", "snsapi_login");
		return super.buildAuthenticateUrl(parameters);
	}

	@Override
	public String buildAuthorizeUrl(OAuth2Parameters parameters) {
		return buildAuthenticateUrl(parameters);
	}

	/**
	 * Add relative HttpMessageConverter to RestTemplate considering that contentType returned from WeChat is html/text.
	 */
	@Override
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = super.createRestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter() {
			@Override
			public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
				List<MediaType> mediaTypes = new ArrayList<>();
				mediaTypes.add(MediaType.TEXT_PLAIN);
				super.setSupportedMediaTypes(mediaTypes);
			}
		});
		return restTemplate;
	}
}
