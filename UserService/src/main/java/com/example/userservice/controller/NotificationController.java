package com.example.userservice.controller;

import com.example.userservice.dto.NotificationRequest;
import com.example.userservice.event.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request){
        UserEvent event = new UserEvent(
                request.getEmail(),
                request.getEventType()
        );
        kafkaTemplate.send("user-events", event);
        return  ResponseEntity.ok("Notification queued");
    }


}
