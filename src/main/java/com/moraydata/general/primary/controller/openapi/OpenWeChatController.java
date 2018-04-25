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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.HttpUrlUtils;
import com.moraydata.general.management.util.ResponseEntity;
import com.moraydata.general.management.util.RestTemplateUtils;
import com.moraydata.general.management.util.WeChatTokenHelper;
import com.moraydata.general.management.util.WeChatTokenHelper.Token;
import com.moraydata.general.management.websocket.DefaultWebSocketHandler;
import com.moraydata.general.management.wechat.Menu;
import com.moraydata.general.management.wechat.MenuButton;
import com.moraydata.general.management.wechat.NavigationMenu;
import com.moraydata.general.management.wechat.SubMenuButton;
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
	
	private static final String SEND_TEMPLATE_MESSAGE_PATH = "https://api.weixin.qq.com/cgi-bin/message/template/send";
	
	@SuppressWarnings({ "serial" })
	@GetMapping("/wechat/sendTemplateMessage")
	public boolean sendTemplateMessage(@RequestParam String openId) {
		if (StringUtils.isBlank(openId)) {
			return false;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("touser", "oQFmP01E8wBBiJMVCh_wQbcitOz0"); // 接收者openId
		parameters.put("template_id", "lvUC1MN9F2JC4Ymb5HYJ-qsocKKfk5kPfCHzzqq3uAM"); // 模板Id
		parameters.put("url", "http://www.baidu.com");
		parameters.put("data", new HashMap<String, Map<String, String>>() {{
			put("first", new HashMap<String, String>() {{
				put("value", "您提交的测试任务已完成! test");
				put("color", "#173177");
			}});
			put("keyword1", new HashMap<String, String>() {{
				put("value", "2018年04月23日 11:32 test");
				put("color", "#173177");
			}});
			put("keyword2", new HashMap<String, String>() {{
				put("value", "2018年04月23日 11:32 test");
				put("color", "#173177");
			}});
			put("keyword3", new HashMap<String, String>() {{
				put("value", "测试未通过! test");
				put("color", "#173177");
			}});
			put("remark", new HashMap<String, String>() {{
				put("value", "点击查看测试结果详情 test");
				put("color", "#173177");
			}});
		}});
		JSONObject jsonResponse = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integrateUrlWithAccessToken(SEND_TEMPLATE_MESSAGE_PATH), RestTemplateUtils.getHttpEntity(parameters), JSONObject.class);
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
    @SuppressWarnings({ "serial" })
	public String createTempTicket(String accessToken, String expireSeconds, int sceneId) {  
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("action_name", "QR_SCENE"); // QR_SCENE代表临时二维码
		parameters.put("expire_seconds", expireSeconds);
		parameters.put("action_info", new HashMap<String, Map<String, Integer>>() {{
			put("scene", new HashMap<String, Integer>() {{
				put("scene_id", sceneId);
			}});
		}});
		JSONObject response = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integrateUrlWithAccessToken(ticketUrl, accessToken), RestTemplateUtils.getHttpEntity(parameters), JSONObject.class);
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
				} else if (event.equals("VIEW")) {
					// 点击公众号菜单跳转到特定URL
	        	} else if (event.equals("CLICK")) {
	        		// 点击公众号菜单执行特定点击事件
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
	
	@Transactional(readOnly = false)
	private void processScanLoginScene(String sceneId, String fromUserName) throws Exception {
		if (sceneId.endsWith(Constants.WECHAT.SCAN_LOGIN_SCENE_ID_FUNCTIONALITY_KEY)) {
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
		} else if (sceneId.endsWith(Constants.WECHAT.SCAN_BIND_WECHAT_SCENE_ID_FUNCTIONALITY_KEY)) {
			// 通过前台个人中心扫码绑定微信
			DefaultWebSocketHandler.sendMessageToBindWeChat(sceneId + Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR + fromUserName);
		} else {
			
		}
	}

	private User createTargetUser(User targetUser) {
		return userService.create(targetUser);
	}

	private User getTargetUser(String fromUserName) throws Exception {
		return userService.getByOpenId(fromUserName);
	}
	
	/*************************************************************** 服务号菜单 ***************************************************************/
	
	private static final String CREATE_MENU_PATH = "https://api.weixin.qq.com/cgi-bin/menu/create";
	
	@GetMapping("/wechat/createMenu")
	public ResponseEntity createMenu(@RequestParam(required = false) String jsonStringMenu) {
/**
		{"button": [
				{"name": "海鳗云",
				"sub_button": [
					{"key": "11", "name": "关于海鳗云", "type": "click"},
					{"key": "12", "name": "产品介绍", "type": "click"},
					{"key": "13", "name": "海鳗云动态", "type": "click"},
					{"key": "14", "name": "商务合作", "type": "click"}
				]},
				{"name": "个人中心",
				"sub_button": [
					{"key": "21", "name": "账号信息", "type": "click"},
					{"key": "22", "name": "用户权限", "type": "click"},
					{"key": "23", "name": "通知管理", "type": "click"}
				]},
				{"key": "30", "name": "历史数据", "type": "click"}
		]}
**/
		if (StringUtils.isBlank(jsonStringMenu)) {
			String clickEvent = MenuButton.Type.CLICK.getValue();
			Menu menu = new Menu();

	        // 建3个导航菜单
	        NavigationMenu navFirst = new NavigationMenu();
	        navFirst.setName("海鳗云");
	        NavigationMenu navSecond = new NavigationMenu();
	        navSecond.setName("个人中心");
	        SubMenuButton navThird = new SubMenuButton();
	        navThird.setType(MenuButton.Type.VIEW.getValue());
//			navThird.setType(clickEvent);
	        navThird.setName("历史数据");
//	        navThird.setKey("30");
	        navThird.setUrl("http://57166643.ngrok.io/wechat/testMenuUrl?content=abc123");

	        // 第一个导航菜单的子菜单
	        SubMenuButton subMenuFirstFirst = new SubMenuButton();
	        subMenuFirstFirst.setType(clickEvent);
	        subMenuFirstFirst.setName("关于海鳗云");
	        subMenuFirstFirst.setKey("11");

	        SubMenuButton subMenuFirstSecond = new SubMenuButton();
	        subMenuFirstSecond.setType(clickEvent);
	        subMenuFirstSecond.setName("产品介绍");
	        subMenuFirstSecond.setKey("12");
	        
	        SubMenuButton subMenuFirstThird = new SubMenuButton();
	        subMenuFirstThird.setType(clickEvent);
	        subMenuFirstThird.setName("海鳗云动态");
	        subMenuFirstThird.setKey("13");
	        
	        SubMenuButton subMenuFirstFourth = new SubMenuButton();
	        subMenuFirstFourth.setType(clickEvent);
	        subMenuFirstFourth.setName("商务合作");
	        subMenuFirstFourth.setKey("14");

	        // 加入导航菜单
	        navFirst.setSub_button(new SubMenuButton[] {subMenuFirstFirst, subMenuFirstSecond, subMenuFirstThird, subMenuFirstFourth});

	        // 第二 个导航菜单的子菜单
	        SubMenuButton subMenuSecondFirst = new SubMenuButton();
	        subMenuSecondFirst.setType(clickEvent);
	        subMenuSecondFirst.setName("账号信息");
	        subMenuSecondFirst.setKey("21");

	        SubMenuButton subMenuSecondSecond = new SubMenuButton();
	        subMenuSecondSecond.setType(clickEvent);
	        subMenuSecondSecond.setName("用户权限");
	        subMenuSecondSecond.setKey("22");

	        SubMenuButton subMenuSecondThird = new SubMenuButton();
	        subMenuSecondThird.setType(clickEvent);
	        subMenuSecondThird.setName("通知管理");
	        subMenuSecondThird.setKey("23");

	        // 加入导航菜单
	        navSecond.setSub_button(new SubMenuButton[] {subMenuSecondFirst, subMenuSecondSecond, subMenuSecondThird});
	        menu.setButton(new MenuButton[] {navFirst, navSecond, navThird});
	        jsonStringMenu = JSON.toJSONString(menu);
		}

		JSONObject response = RestTemplateUtils.INSTANCE.getRestTemplate().postForObject(integrateUrlWithAccessToken(CREATE_MENU_PATH), RestTemplateUtils.getHttpEntity(jsonStringMenu), JSONObject.class);
		return "ok".equals(response.getString("errmsg")) ? ResponseEntity.success("重建服务号菜单成功", jsonStringMenu) : ResponseEntity.error("重建服务号菜单失败", response);
	}
	
	@GetMapping("/wechat/testMenuUrl")
	public ResponseEntity testMenuUrl(@RequestParam String content) {
		return ResponseEntity.success("微信服务号菜单点击跳转URL功能测试成功", content);
	}

	private String integrateUrlWithAccessToken(final String baseUrl) {
		return integrateUrlWithAccessToken(baseUrl, handler.getToken().getAccessToken());
	}
	
	@SuppressWarnings("serial")
	private String integrateUrlWithAccessToken(final String baseUrl, String accessToken) {
		String integratedUrl = HttpUrlUtils.integrate(baseUrl, new HashMap<String, Object>() {{
        	put(Constants.WECHAT.ACCESS_TOKEN, accessToken);
        }});
		return integratedUrl;
	}
}
