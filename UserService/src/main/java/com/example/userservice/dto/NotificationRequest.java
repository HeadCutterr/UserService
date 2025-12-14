package com.example.userservice.dto;

import com.example.userservice.event.UserEvent;
import lombok.Data;

@Data
public class NotificationRequest {
    private String email;
    private UserEvent.EventType eventType;
}


