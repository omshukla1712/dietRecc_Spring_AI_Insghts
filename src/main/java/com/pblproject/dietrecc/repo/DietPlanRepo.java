package com.pblproject.dietrecc.repo;

import com.pblproject.dietrecc.model.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DietPlanRepo extends JpaRepository<DietPlan,Long> {
    // Custom query to find all plans for a specific user, sorted by newest first
    List<DietPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
}
