package com.example.userservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String email;
    private EventType eventType;

    public enum EventType {
        @JsonProperty("CREATE")
        CREATE,
        @JsonProperty("DELETE")
        DELETE
    }
}