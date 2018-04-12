package com.moraydata.general.primary.controller.openapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.HttpUrlUtils;
import com.moraydata.general.management.util.RestTemplateUtils;
import com.moraydata.general.management.util.WeChatTokenHelper;
import com.moraydata.general.management.util.WeChatTokenHelper.Token;
import com.moraydata.general.management.websocket.DefaultWebSocketHandler;
import com.moraydata.general.primary.entity.User;
import com.moraydata.general.primary.service.UserService;

@RestController
public class OpenWeChatController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private WeChatTokenHelper handler;
	
	@Value("${project.base.social.user.defaultPassword}")
	private String defaultPassword;
	
	@Value("${project.base.wechat.service.ticketUrl}")
	private String ticketUrl;
	
	@Value("${project.base.wechat.service.showQRCodeUrl}")
	private String showQRCodeUrl;
	
	/************************************************************** 发送模板消息 ****************************************************************/
	
	private String send_template_message_path = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	@GetMapping("/wechat/sendTemplateMessage")
	public boolean sendTemplateMessage(@RequestParam String openId) {
		
		if (StringUtils.isBlank(openId)) {
			return false;
		}
		
		String integratedUrl = HttpUrlUtils.integrate(send_template_message_path, new HashMap<String, Object>() {{
        	put("access_token", handler.getToken().getAccessToken());
        }});
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("touser", openId); // 接收者openId
		parameters.put("template_id", "A8DciNBr821A0jtVEBNBHCG6Lzu0Iz4iiRSKCotgqhw"); // 模板Id
		parameters.put("url", "http://www.baidu.com");
		parameters.put("data", new HashMap<String, Map<String, String>>() {{
			put("first", new HashMap<String, String>() {{
				put("value", "恭喜你被骗成功!");
				put("color", "#173177");
			}});
			put("keyword1", new HashMap<String, String>() {{
				put("value", "比特币");
				put("color", "#173177");
			}});
			put("keyword2", new HashMap<String, String>() {{
				put("value", "No.000001");
				put("color", "#173177");
			}});
			put("keyword3", new HashMap<String, String>() {{
				put("value", "99.98元");
				put("color", "#173177");
			}});
			put("keyword4", new HashMap<String, String>() {{
				put("value", "2018年04月11日");
				put("color", "#173177");
			}});
			put("remark", new HashMap<String, String>() {{
				put("value", "欢迎再次上当!");
				put("color", "#173177");
			}});
		}});
		JSONObject jsonResponse = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integratedUrl, new HttpEntity(parameters, new HttpHeaders()), JSONObject.class);
		return jsonResponse.getString("errmsg").equals("ok");
	}
	
	
	/************************************************************** 关注服务号并且自动登录系统 ****************************************************************/
	
	@GetMapping("/wechat/getQRCode")
    public String getQrcode(@RequestParam(value = "sceneId") int sceneId) throws Exception{
		Token token = handler.getToken();
        String ticket = createTempTicket(token.getAccessToken(), String.valueOf(token.getExpiresIn()), sceneId);
        return ticket;
    }

	/** 
     * 创建临时带参数二维码 
     * @param accessToken 连接令牌
     * @expireSeconds 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。 
     * @param sceneId 场景Id 
     * @return 能够显示二维码的url
     */  
    @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
	public String createTempTicket(String accessToken, String expireSeconds, int sceneId) {  
        String integratedUrl = HttpUrlUtils.integrate(ticketUrl, new HashMap<String, Object>() {{
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
		JSONObject response = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integratedUrl, new HttpEntity(parameters, new HttpHeaders()), JSONObject.class);
        return showQRCodeUrl + "?ticket=" + response.getString("ticket");
	}
    
    /**
     * 在微信端配置服务号后台服务器时，微信端会发送Get请求过来以确定是否填入的服务号后台服务器地址可用，此接口只做此用
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
     * 在微信端配置过服务号后台服务器后，所有与服务号有关的事件响应，都将会发送到该接口中
     */
    @PostMapping("/wechat/callback")
    public void callbackPost(@RequestBody(required=false) String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	responseNothing(response); // 5秒钟内快速相应微信端，否则将会被微信端识别为调用失败，紧跟着会有3此重复调用
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
				} else if (event.equals("unsubscribe")) {
					// 取消关注公众号
				} else if (event.equals("SCAN")) {
					// 已关注公众号的扫码行为
					processScanLoginScene(eventKey, fromUserName);
				} else if (event.equals("TEMPLATESENDJOBFINISH")) {
					// template send job finish => 模板消息发送结束
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
			if (targetUser == null) {
				throw new RuntimeException("微信扫码登录过程中自动创建用户记录失败");
			}
		} else {
			// 该用户已经是系统中用户
		}
		// 向前台登录页面发送websocket message，通知前端为用户自动登录
		DefaultWebSocketHandler.sendMessageToAutomaticLogin(sceneId + Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR + fromUserName);
	}

	private User createTargetUser(User targetUser) {
		return userService.create(targetUser);
	}

	private User getTargetUser(String fromUserName) throws Exception {
		return userService.getByOpenId(fromUserName);
	}
}
