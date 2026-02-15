package com.pblproject.dietrecc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Column(nullable = false)
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Min(value = 18, message = "Age must be valid")
    private int age;

    @Column(name = "height_cm")
    @Positive(message = "Height must be positive")
    private double height;

    @Column(name = "weight_kg")
    @Positive(message = "Weight must be positive")
    private double weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Goal goal;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    public enum ActivityLevel {
        SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTRA_ACTIVE
    }

    public enum Goal {
        LOSE_WEIGHT, MAINTAIN_WEIGHT, GAIN_MUSCLE
    }
    public enum Gender{
        MALE, FEMALE
    }
}
