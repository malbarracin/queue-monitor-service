package io.queuemonitor.service;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

/**
 * Service that monitors an AWS SQS queue using LocalStack. It listens for new
 * messages in the queue and sends them in real-time via WebSocket to a
 * connected client.
 * 
 * The queue and AWS credentials are configured via Spring's @Value annotations.
 */
@Service
public class SqsQueueMonitoringService {

	private static final Logger logger = LoggerFactory.getLogger(SqsQueueMonitoringService.class);

	@Value("${monitor.aws.sqs-url}")
	private String QUEUE_URL;

	@Value("${monitor.cloud.aws.credentials.accessKey}")
	private String AWS_ACCESS_KEY_ID;

	@Value("${monitor.cloud.aws.credentials.secretKey}")
	private String AWS_SECRET_ACCESS_KEY;

	@Value("${monitor.cloud.aws.region.static}")
	private String DEFAULT_REGION;

	private SqsClient sqsClient;
	private WebSocketSession currentSession;

	/**
	 * Constructor that initializes the SQS client using AWS credentials and
	 * LocalStack as the endpoint override.
	 */
	@PostConstruct
	public void SqsQueueMonitoringService() {

		logger.info("AWS Access Key: {}", AWS_ACCESS_KEY_ID);
		logger.info("AWS Secret Key: {}", AWS_SECRET_ACCESS_KEY);
		logger.info("SQS URL: {}", QUEUE_URL);
		logger.info("Region: {}", DEFAULT_REGION);

		if (DEFAULT_REGION == null || DEFAULT_REGION.isEmpty()) {
			throw new IllegalArgumentException("La región no puede ser null o vacía");
		}

		this.sqsClient = SqsClient.builder().region(Region.of(DEFAULT_REGION)) // Use the region from the configuration
				.credentialsProvider(StaticCredentialsProvider
						.create(AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)))
				.endpointOverride(URI.create("http://localhost:4566")) // LocalStack endpoint
				.build();
	}

	/**
	 * Starts the monitoring of the SQS queue in a separate thread. The method is
	 * invoked after the service bean is initialized.
	 */
	@PostConstruct
	public void startMonitoring() {
		new Thread(() -> {
			try {
				monitorSqsQueue();
			} catch (IOException e) {
				logger.error("Error while monitoring SQS queue: ", e);
			}
		}).start();
	}

	/**
	 * Monitors the AWS SQS queue for new messages. When a message is received, it
	 * is sent to the connected WebSocket client. If there are no messages, it waits
	 * 10 seconds before checking again.
	 * 
	 * @throws IOException if there is an error sending the message via WebSocket
	 */
	public void monitorSqsQueue() throws IOException {
		while (true) {
			logger.info("Polling SQS queue: {}", QUEUE_URL);
			ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(QUEUE_URL)
					.maxNumberOfMessages(5).waitTimeSeconds(10).build();

			List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

			for (Message message : messages) {
				logger.info("Message received from SQS: {}", message.body());

				if (currentSession != null && currentSession.isOpen()) {
					logger.info("Sending message to WebSocket client");
					currentSession.sendMessage(new TextMessage(message.body()));
				} else {
					logger.warn("No WebSocket session available or session is closed");
				}

				// Delete the message from the queue after it's processed
				sqsClient.deleteMessage(builder -> builder.queueUrl(QUEUE_URL).receiptHandle(message.receiptHandle()));
				logger.info("Message deleted from SQS queue");
			}

			try {
				Thread.sleep(5000); // Delay between polling
			} catch (InterruptedException e) {
				logger.error("Thread sleep interrupted: ", e);
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Sets the current WebSocket session, used to send messages to the client.
	 * 
	 * @param session the WebSocketSession to send messages to
	 */
	public void setWebSocketSession(WebSocketSession session) {
		this.currentSession = session;
		logger.info("WebSocket session set: {}", session.getId());
	}
}
