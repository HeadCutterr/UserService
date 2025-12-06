package com.example.userservice.dto;

import jakarta.validation.constraints.*;

public record UserCreateDTO(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 120, message = "Age must be at most 120")
        Integer age
) {
}