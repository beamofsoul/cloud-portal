package com.moraydata.general.management.websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.moraydata.general.management.util.Constants;
import com.moraydata.general.management.util.SpringUtils;
import com.moraydata.general.primary.entity.query.QUser;
import com.moraydata.general.primary.repository.UserRepository;
import com.moraydata.general.primary.service.UserService;

@Async
@Component
public class DefaultWebSocketHandler implements WebSocketHandler {

	private static AtomicInteger onlineCount = new AtomicInteger(0); 
	private static CopyOnWriteArraySet<WebSocketSession> webSocketSet = new CopyOnWriteArraySet<>();
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		webSocketSet.remove(session);
		onlineCount.decrementAndGet();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		webSocketSet.add(session);
		onlineCount.incrementAndGet();
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		if (session.isOpen()) {
			synchronized (session) {
				String payload = message.getPayload().toString();
				if (payload.startsWith("sendAll#")) {
					sendMessageToAll(new TextMessage(payload.split("#")[1]));
				} else {
					// 2018-04-16 addition to support binding WeChat by scanning QR code
					if (payload.indexOf(Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR) > -1) {
						if (payload.indexOf(Constants.WECHAT.SCAN_BIND_WECHAT_KEY) > -1) {
							String[] mixedValues = payload.split(Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR);
							String username = mixedValues[1];
							session.getAttributes().put(Constants.WECHAT.SCAN_USERNAME_KEY, username);
						}
					} else {
						// Set an attribute to current session for each login page on PC ends. 
						// The value of payload from PC ends will be a random integer which will be used to ensure uniqueness of sceneId.
						session.getAttributes().put(Constants.WECHAT.SCAN_SCENE_ID_KEY, payload);					
					}
	
					session.sendMessage(new TextMessage("Server has received message : " + payload));
				}
			}
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		exception.printStackTrace();
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
	
	public static void sendMessageToAutomaticLogin(String sceneId) throws Exception {
		// The value of input parameter "sceneId" will be formatted as [unicode/sceneId + "####" + openId].
		String[] mixedValues = sceneId.split(Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR);
		String unicode = mixedValues[0];
		String openId = mixedValues[1];
		// Find out which web socket session has an attribute which is same as the last part of given sceneId.
		// This is the PC end which is required to process scanning login functionality.
		webSocketSet.stream().filter(findOutCurrentSession(unicode)).forEach(e -> {
			if (e.isOpen()) {
				try {
					// Send a text message of openId to the PC end to make sure it has able to login automatically for the user.
					e.sendMessage(new TextMessage(Constants.WECHAT.SCAN_LOGIN_WEB_SOCKET_COMMAND + Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR + openId));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	public static void sendMessageToBindWeChat(String sceneId) throws Exception {
		// The value of input parameter "sceneId" will be formatted as [unicode/sceneId + "####" + openId].
		String[] mixedValues = sceneId.split(Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR);
		String unicode = mixedValues[0];
		String openId = mixedValues[1];
		// Find out which web socket session has an attribute which is same as the last part of given sceneId.
		// This is the PC end which is required to process scanning binding WeChat functionality.
		webSocketSet.stream().filter(findOutCurrentSession(unicode)).forEach(e -> {
			if (e.isOpen()) {
				try {
					if (e.getAttributes().containsKey(Constants.WECHAT.SCAN_USERNAME_KEY) || e.getAttributes().get(Constants.WECHAT.SCAN_USERNAME_KEY) == null) {
						Object usernameObject = e.getAttributes().get(Constants.WECHAT.SCAN_USERNAME_KEY);
						UserService service = SpringUtils.getBean(UserService.class);
						UserRepository repository = SpringUtils.getBean(UserRepository.class);
						// Check whether given openId has been used
						boolean exists = repository.exists(QUser.user.openId.eq(openId));
						if (exists) {
							// If used, warn it
							e.sendMessage(new TextMessage(Constants.WECHAT.SCAN_BIND_WECHAT_KEY + Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR + Constants.RESPONSE_ENTITY.ERROR));
							return;
						}
						service.bindWeChat(usernameObject.toString(), openId);
						e.sendMessage(new TextMessage(Constants.WECHAT.SCAN_BIND_WECHAT_KEY + Constants.WECHAT.SCAN_LOGIN_DEFAULT_SEPARATOR + openId));
					} else {
						throw new RuntimeException("Failed to bind WeChat for current login user, because its' username cannot be found in its' session");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private static Predicate<? super WebSocketSession> findOutCurrentSession(String unicode) {
		return e -> (e.getAttributes().containsKey(Constants.WECHAT.SCAN_SCENE_ID_KEY) && e.getAttributes().get(Constants.WECHAT.SCAN_SCENE_ID_KEY).equals(unicode));
	}
	
	public static void sendMessageToAll(WebSocketMessage<?> message) {
		for (WebSocketSession session : webSocketSet) {
			try {
				if (session.isOpen()) {
					session.sendMessage(new TextMessage(message.getPayload().toString()));
//					System.out.println("#### WEBSOCKET MESSAGE TO ALL("+session.getPrincipal().getName()+"): " + message.getPayload().toString());
				}
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	public static Integer getOnlineCount() {
		return onlineCount.get();
}

}
