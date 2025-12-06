package com.example.userservice.dto;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {
}