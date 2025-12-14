package com.example.notificationservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEvent {
    private String email;
    private EventType eventType;

    public UserEvent() {
    }

    public UserEvent(String email, EventType eventType) {
        this.email = email;
        this.eventType = eventType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public enum EventType {
        @JsonProperty("CREATE")
        CREATE,
        @JsonProperty("DELETE")
        DELETE
    }
}