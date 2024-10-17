package io.queuemonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the QueueMonitorService application. This is the entry point
 * for the Spring Boot application, which monitors an SQS queue and sends
 * real-time updates via WebSocket.
 */
@SpringBootApplication
public class QueueMonitorServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(QueueMonitorServiceApplication.class);

	/**
	 * The main method that starts the Spring Boot application.
	 * 
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		logger.info("Starting QueueMonitorServiceApplication...");
		SpringApplication.run(QueueMonitorServiceApplication.class, args);
		logger.info("QueueMonitorServiceApplication started successfully.");
	}
}
