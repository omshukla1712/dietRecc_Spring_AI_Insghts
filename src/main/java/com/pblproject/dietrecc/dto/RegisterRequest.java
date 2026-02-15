package com.pblproject.dietrecc.dto;

import com.pblproject.dietrecc.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 chars")
    private String password;

    @Min(value = 18, message = "Age must be 18+")
    private int age;

    @Positive
    private double height;

    @Positive
    private double weight;

    @NotNull(message = "Activity level is required")
    private User.ActivityLevel activityLevel;

    @NotNull(message = "Goal is required")
    private User.Goal goal;

    @NotNull(message = "Gender is required")
    private User.Gender gender;
}
