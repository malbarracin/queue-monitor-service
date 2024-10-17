package io.queuemonitor.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.queuemonitor.service.SqsQueueMonitoringService;

/**
 * REST controller for starting and managing the SQS queue monitoring. This
 * controller exposes a simple endpoint to start monitoring the SQS queue.
 */
@RestController
public class QueueController {

	private final SqsQueueMonitoringService monitoringService;

	/**
	 * Constructor that injects the SqsQueueMonitoringService.
	 *
	 * @param monitoringService the service that monitors the SQS queue
	 */
	@Autowired
	public QueueController(SqsQueueMonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	/**
	 * Endpoint to start monitoring the SQS queue. This method triggers the
	 * monitoring service to begin listening to messages from the SQS queue.
	 *
	 * @return a confirmation message indicating that monitoring has started
	 */
	@GetMapping("/startMonitoring")
	public String startMonitoring() {
		try {
			monitoringService.monitorSqsQueue();
		} catch (IOException e) {
			e.printStackTrace(); // Logs the stack trace if there is an IOException
		}
		return "Monitoreo de SQS iniciado.";
	}
}
