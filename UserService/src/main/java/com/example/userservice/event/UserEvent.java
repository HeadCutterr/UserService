package com.example.userservice.event;

import com.fasterxml.jackson.annotation.JsonProperty; // Добавь импорт
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
        @JsonProperty("CREATE") // Добавь аннотацию
        CREATE,
        @JsonProperty("DELETE") // Добавь аннотацию
        DELETE
    }
}