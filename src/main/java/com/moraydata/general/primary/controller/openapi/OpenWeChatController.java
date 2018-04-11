package com.moraydata.general.primary.controller.openapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.HttpUrlUtils;
import com.moraydata.general.management.websocket.DefaultWebSocketHandler;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.entity.UserRole;
import com.moraydata.general.primary.service.RoleService;
import com.moraydata.general.primary.service.UserRoleService;
import com.moraydata.general.primary.service.UserService;

@RestController
public class OpenWeChatController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${project.base.social.user.defaultPassword}")
	private String defaultPassword;
	
	// 创建二维码  
    private String create_ticket_path = "https://api.weixin.qq.com/cgi-bin/qrcode/create";  
    // 通过ticket换取二维码  
    private String showqrcode_path = "https://mp.weixin.qq.com/cgi-bin/showqrcode"; 
    // 通过appId和appSecret获取access_token
    private String access_token_url = "https://api.weixin.qq.com/cgi-bin/token";
    // 微信服务号appId
	private String appId = "wx1fb4cf9f5f3385e7";
	// 微信服务号appSecret
	private String appSecret = "3889e726e00f19662e8b7e9cde4b5599";
	
	@SuppressWarnings({ "unchecked", "serial" })
	@GetMapping("/wechat/getQRCode")
    public String getQrcode(@RequestParam(value = "sceneId") int sceneId) throws Exception{
		String integratedUrl = HttpUrlUtils.integrate(access_token_url, new HashMap<String, Object>() {{
			put("grant_type", "client_credential");
			put("appid", appId);
			put("secret", appSecret);
		}});
		Map<String, Object> response = getRestTemplate().getForObject(integratedUrl, Map.class);
        String ticket = createTempTicket(response.get("access_token").toString(),response.get("expires_in").toString(),sceneId);
        return ticket;
    }

	private RestTemplate getRestTemplate() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().set(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return template;
	}
	
	/** 
     * 创建临时带参数二维码 
     * @param accessToken 
     * @expireSeconds 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。 
     * @param sceneId 场景Id 
     * @return 
     */  
    @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
	public String createTempTicket(String accessToken, String expireSeconds, int sceneId) {  
        String integratedUrl = HttpUrlUtils.integrate(create_ticket_path, new HashMap<String, Object>() {{
        	put("access_token", accessToken);
        }});
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action_name", "QR_SCENE"); // QR_SCENE代表临时二维码
		parameters.put("expire_seconds", expireSeconds);
		parameters.put("action_info", new HashMap<String, Map<String, Integer>>() {{
			put("scene", new HashMap<String, Integer>() {{
				put("scene_id", sceneId);
			}});
		}});
		JSONObject response = getRestTemplate().postForObject(integratedUrl, new HttpEntity(parameters, new HttpHeaders()), JSONObject.class);
        return showqrcode_path + "?ticket=" + response.getString("ticket");
	}
    
    /**
     signature:4777ac95013d0a6d47fcf375df152e458a407a8c
	 timestamp:1523323909
	 nonce:3688354833
	 echostr:16926872307383547597
    	 
     * @param body
     * @param request
     * @param response
     * @return void
     * @throws IOException
     */
    @GetMapping("/wechat/callback")
    public void callback(@RequestBody(required=false) String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
//    	String signature = request.getParameter("signature");
//    	String timestamp = request.getParameter("timestamp");
//    	String nonce = request.getParameter("nonce");
    	String echostr = request.getParameter("echostr");
//    	System.out.println("signature:" + signature);
//    	System.out.println("timestamp:" + timestamp);
//    	System.out.println("nonce:" + nonce);
//    	System.out.println("echostr:" + echostr);
    	PrintWriter pw = response.getWriter();
    	pw.append(echostr);
    	pw.flush();
    	pw.close();
    }
    
    /**
	<xml><ToUserName><![CDATA[gh_e75417a927d6]]></ToUserName>
    <FromUserName><![CDATA[ovJ_C1J964Y_BKS08GyMN7jXa5eI]]></FromUserName>
    <CreateTime>1523324812</CreateTime>
    <MsgType><![CDATA[event]]></MsgType>
    <Event><![CDATA[unsubscribe]]></Event>
    <EventKey><![CDATA[]]></EventKey>
    </xml>
    ==============================================微信URL回掉测试=================================================
    ToUserName===gh_e75417a927d6
    FromUserName===ovJ_C1J964Y_BKS08GyMN7jXa5eI
    CreateTime===1523324812
    MsgType===event
    Event===unsubscribe
    EventKey===
    Ticket===null
 
    <xml><ToUserName><![CDATA[gh_e75417a927d6]]></ToUserName>
    <FromUserName><![CDATA[ovJ_C1J964Y_BKS08GyMN7jXa5eI]]></FromUserName>
    <CreateTime>1523324818</CreateTime>
    <MsgType><![CDATA[event]]></MsgType>
    <Event><![CDATA[subscribe]]></Event>
    <EventKey><![CDATA[qrscene_911]]></EventKey>
    <Ticket><![CDATA[gQFe8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyY2c0b0VXWXZmMmsxT3dQYzFxMXoAAgSAF8xaAwQgHAAA]]></Ticket>
    </xml>
    ==============================================微信URL回掉测试=================================================
    ToUserName===gh_e75417a927d6
    FromUserName===ovJ_C1J964Y_BKS08GyMN7jXa5eI
    CreateTime===1523324818
    MsgType===event
    Event===subscribe
    EventKey===qrscene_911
    Ticket===gQFe8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyY2c0b0VXWXZmMmsxT3dQYzFxMXoAAgSAF8xaAwQgHAAA
    
    <xml><ToUserName><![CDATA[gh_e75417a927d6]]></ToUserName>
    <FromUserName><![CDATA[ovJ_C1J964Y_BKS08GyMN7jXa5eI]]></FromUserName>
    <CreateTime>1523343754</CreateTime>
    <MsgType><![CDATA[event]]></MsgType>
    <Event><![CDATA[SCAN]]></Event>
    <EventKey><![CDATA[911]]></EventKey>
    <Ticket><![CDATA[gQH27zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyZ2t6ckVyWXZmMmsxU0FaYzFxMVAAAgSEYcxaAwQgHAAA]]></Ticket>
    </xml>
    ==============================================微信URL回掉测试=================================================
    ToUserName===gh_e75417a927d6
    FromUserName===ovJ_C1J964Y_BKS08GyMN7jXa5eI
    CreateTime===1523343754
    MsgType===event
    Event===SCAN
    EventKey===911
    Ticket===gQH27zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyZ2t6ckVyWXZmMmsxU0FaYzFxMVAAAgSEYcxaAwQgHAAA
            
     * @param body
     * @param request
     * @param response
     * @return void
     * @throws IOException
     */
    @PostMapping("/wechat/callback")
    public void callbackPost(@RequestBody(required=false) String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	/**
    	 * 用户未关注时:
    	 * 
    	 * ToUserName: 开发者微信号
    	 * FromUserName: 发送方账号(一个OpenID)
    	 * CreateTime: 消息创建时间(整形)
    	 * MsgType: 消息类型, event
    	 * Event: 事件类型, subscribe
    	 * EventKey: 事件KEY值, qrscene_为前缀, 后面为二维码的参数值
    	 * Ticket: 二维码的ticket, 可用来换取二维码图片
    	 */
    	
    	/**
    	 * 用户已关注时:
    	 * 
    	 * ToUserName: 开发者微信号
    	 * FromUserName: 发送方账号(一个OpenID)
    	 * CreateTime: 消息创建时间(整形)
    	 * MsgType: 消息类型, event
    	 * Event: 事件类型, SCAN
    	 * EventKey: 事件KEY值, 是一个32位无符号整数, 即创建二维码时的二维码scene_id
    	 * Ticket: 二维码的ticket, 可用来换取二维码图片	
    	 */
    	SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(new ByteArrayInputStream(body.toString().getBytes("UTF-8")));
            Element rootElt = document.getRootElement();
            
//	        String toUserName = rootElt.elementText("ToUserName");
	        String fromUserName = rootElt.elementText("FromUserName");
//	        Date createTime = new Date(Integer.valueOf(rootElt.elementText("CreateTime")));
	        String msgType = rootElt.elementText("MsgType");
	        String event = rootElt.elementText("Event");
	        String eventKey = rootElt.elementText("EventKey");
//	        String ticket = rootElt.elementText("Ticket");
	        
	        if (msgType.equals("event")) {
				if (event.equals("subscribe")) {
					// 关注公众号
					String sceneId = eventKey.split("_")[1];
					processScanLoginScene(sceneId, fromUserName);
					responseNothing(response);
				} else if (event.equals("unsubscribe")) {
					// 取消关注公众号
				} else if (event.equals("SCAN")) {
					// 已关注公众号的扫码行为
					processScanLoginScene(eventKey, fromUserName);
					responseNothing(response);
				} else {
					throw new RuntimeException(String.format("未知的微信扫码事件类型: %s", event));
				}
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }

    /**
     * 在五秒钟之内快速给微信端回复，内容无所谓，以免微信端以为当前程序接受失败，会继续发送请求
     * @param response
     * @throws IOException
     */
	private void responseNothing(HttpServletResponse response) throws IOException {
		PrintWriter pw = response.getWriter();
		pw.append("");
		pw.flush();
		pw.close();
	}
	
	@Transactional
	private void processScanLoginScene(String sceneId, String fromUserName) throws Exception {
		// 通过前台页面扫码登录
		User targetUser = getTargetUser(fromUserName);
		if (targetUser == null) {
			// 该用户未在系统中注册，则创建用户账号
			targetUser = User
							.builder()
							.username(fromUserName)
							.password(passwordEncoder.encode(defaultPassword))
							.openId(fromUserName)
							.status(User.Status.NORMAL.getValue())
							.build();
			targetUser = createTargetUser(targetUser);
			if (targetUser != null) {
				createTargetUserRole(targetUser);
			} else {
				throw new RuntimeException("微信扫码登录过程中自动创建用户记录失败");
			}
		} else {
			// 该用户已经是系统中用户
		}
		DefaultWebSocketHandler.sendMessageToAutomaticLogin(sceneId + Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR + fromUserName);
	}

	private void createTargetUserRole(User targetUser) {
		userRoleService.create(new UserRole(targetUser, roleService.get(Constants.ROLE.TRIAL_ROLE_NAME)));
	}

	private User createTargetUser(User targetUser) {
		return userService.create(targetUser);
	}

	private User getTargetUser(String fromUserName) throws Exception {
		return userService.getByOpenId(fromUserName);
	}
}
