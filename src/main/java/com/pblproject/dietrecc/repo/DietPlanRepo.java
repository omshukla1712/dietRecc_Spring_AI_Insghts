package com.pblproject.dietrecc.repo;

import com.pblproject.dietrecc.model.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DietPlanRepo extends JpaRepository<DietPlan,Long> {
    // Custom query to find all plans for a specific user, sorted by newest first
    List<DietPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    // Grabs only the single most recent plan for the user
    Optional<DietPlan> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
