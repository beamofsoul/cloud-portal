package com.moraydata.general.management.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class DefaultWebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private WebSocketHandler defaultWebSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry ws) {
		ws.addHandler(defaultWebSocketHandler, "/sockjs").setAllowedOrigins("*").withSockJS();
	}
}
