package io.queuemonitor.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import io.queuemonitor.service.SqsQueueMonitoringService;

/**
 * Handler for managing WebSocket connections and messages. This class handles
 * the connection establishment, manages incoming text messages, and passes
 * WebSocket sessions to the SqsQueueMonitoringService for real-time monitoring.
 */
@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageHandler.class);

	private final SqsQueueMonitoringService monitoringService;

	/**
	 * Constructor for WebSocketMessageHandler.
	 * 
	 * @param monitoringService the service that handles SQS queue monitoring
	 */
	public WebSocketMessageHandler(SqsQueueMonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	/**
	 * Invoked after a WebSocket connection has been established. This method sends
	 * a confirmation message to the client and registers the session with the
	 * monitoring service to allow real-time queue monitoring.
	 * 
	 * @param session the WebSocket session that was established
	 * @throws Exception if an error occurs during message sending
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("WebSocket connection established. Session ID: {}", session.getId());

		// Send a welcome message to the client
		session.sendMessage(new TextMessage("Conexión establecida. Monitoreando colas..."));
		logger.info("Sent confirmation message to WebSocket client.");

		// Register the session with the monitoring service to track queue messages
		monitoringService.setWebSocketSession(session);
		logger.info("WebSocket session registered in the monitoring service.");
	}

	/**
	 * Handles incoming text messages from the WebSocket client.
	 * 
	 * @param session the WebSocket session
	 * @param message the incoming message from the client
	 * @throws IOException if an error occurs during message handling
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
		logger.info("Received message from WebSocket client: {}", message.getPayload());
		// You can handle the incoming messages from the client here if necessary.
	}
}
