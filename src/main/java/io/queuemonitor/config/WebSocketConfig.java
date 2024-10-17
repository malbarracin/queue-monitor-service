package io.queuemonitor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import io.queuemonitor.handler.WebSocketMessageHandler;

/**
 * Configuration class for WebSocket in the Queue Monitor service. It registers
 * the WebSocket handler that manages connections and messages for real-time
 * queue monitoring.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

	private final WebSocketMessageHandler webSocketMessageHandler;

	/**
	 * Constructor that injects the WebSocketMessageHandler used to handle WebSocket
	 * connections and messages.
	 *
	 * @param webSocketMessageHandler the handler that processes WebSocket events
	 */
	public WebSocketConfig(WebSocketMessageHandler webSocketMessageHandler) {
		this.webSocketMessageHandler = webSocketMessageHandler;
		logger.info("WebSocketConfig initialized with WebSocketMessageHandler.");
	}

	/**
	 * Registers the WebSocket handlers, mapping them to the specified endpoint
	 * ("/ws"). The handler will handle all WebSocket messages and manage
	 * connections.
	 * 
	 * @param registry the WebSocketHandlerRegistry where the handler is registered
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		logger.info("Registering WebSocket handler at endpoint: /ws");
		registry.addHandler(webSocketMessageHandler, "/ws").setAllowedOrigins("*"); // Allows any origin to connect (for
																					// testing purposes)
		logger.info("WebSocket handler registered successfully.");
	}
}
