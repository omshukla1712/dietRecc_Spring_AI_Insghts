package com.pblproject.dietrecc.config;

import com.pblproject.dietrecc.model.FoodItem;
import com.pblproject.dietrecc.repo.FoodRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final FoodRepo foodRepository;
    public DataLoader(FoodRepo foodRepo) {
        this.foodRepository = foodRepo;
    }
    @Override
    public void run(String... args) throws Exception {
        // Only load if the DB is empty
        if (foodRepository.count() == 0) {
            System.out.println("Loading initial food data...");

            List<FoodItem> foods = List.of(
                    new FoodItem(null, "Oatmeal", 68, 2, 12, 1, "Breakfast"),
                    new FoodItem(null, "Scrambled Eggs (2)", 180, 12, 2, 14, "Breakfast"),
                    new FoodItem(null, "Grilled Chicken Breast", 165, 31, 0, 3, "Lunch"),
                    new FoodItem(null, "Brown Rice (1 cup)", 216, 5, 45, 1, "Lunch"),
                    new FoodItem(null, "Salmon Fillet", 208, 20, 0, 13, "Dinner"),
                    new FoodItem(null, "Greek Yogurt", 59, 10, 3, 0, "Snack")
            );

            foodRepository.saveAll(foods);
            System.out.println("Data loaded!");
        }
    }
}
