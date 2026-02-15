package com.pblproject.dietrecc.repo;

import com.pblproject.dietrecc.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodRepo extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByCategory(String category);
    List<FoodItem> findByFatLessThan(int maxFat);
    List<FoodItem> findByProteinGreaterThan(int minProtein);
}
