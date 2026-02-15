package com.pblproject.dietrecc.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "diet_plans")
@Data
@NoArgsConstructor
public class DietPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String planDetails;

    private int targetCalories;
    private String goal;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public DietPlan(User user, String planDetails, int targetCalories, String goal) {
        this.user = user;
        this.planDetails = planDetails;
        this.targetCalories = targetCalories;
        this.goal = goal;
    }
}
