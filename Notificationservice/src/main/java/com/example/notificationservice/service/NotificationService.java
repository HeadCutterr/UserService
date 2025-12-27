package com.example.notificationservice.service;

import com.example.notificationservice.event.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final EmailService emailService;

    @Autowired
    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group", containerFactory = "userEventKafkaListenerContainerFactory")
    public void handleUserEvent(UserEvent event) {
        log.info("Received user event: {} for email: {}", event.getEventType(), event.getEmail());

        if (event == null || event.getEmail() == null || event.getEmail().trim().isEmpty()) {
            log.warn("Received invalid user event: {}", event);
            return;
        }

        try {
            switch (event.getEventType()) {
                case CREATE:
                    emailService.sendWelcomeEmail(event.getEmail());
                    log.info("Sent welcome email to: {}", event.getEmail());
                    break;
                case DELETE:
                    emailService.sendDeleteEmail(event.getEmail());
                    log.info("Sent delete email to: {}", event.getEmail());
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to process user event: {}", event, e);
        }
    }
}