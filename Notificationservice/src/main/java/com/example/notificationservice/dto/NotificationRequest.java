package com.example.notificationservice.dto;

import com.example.notificationservice.event.UserEvent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @Email @NotNull String email,
        @NotNull UserEvent.EventType eventType
) {
}