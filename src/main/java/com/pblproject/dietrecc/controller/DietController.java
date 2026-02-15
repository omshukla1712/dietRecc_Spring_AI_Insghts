package com.pblproject.dietrecc.controller;

import com.pblproject.dietrecc.dto.DietResponse;
import com.pblproject.dietrecc.model.User;
import com.pblproject.dietrecc.repo.UserRepo;
import com.pblproject.dietrecc.service.DietAiService; // <-- Updated Import
import com.pblproject.dietrecc.service.DietCalculatorService;
import com.pblproject.dietrecc.repo.DietPlanRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diet")
public class DietController {

    private final UserRepo userRepository;
    private final DietCalculatorService dietCalculator;
    private final DietAiService aiService;
    private final com.pblproject.dietrecc.repo.DietPlanRepo dietPlanRepo;

    public DietController(UserRepo userRepository,
                          DietCalculatorService dietCalculator,
                          DietAiService aiService,
                          com.pblproject.dietrecc.repo.DietPlanRepo dietPlanRepo) { // <--- NEW
        this.userRepository = userRepository;
        this.dietCalculator = dietCalculator;
        this.aiService = aiService;
        this.dietPlanRepo = dietPlanRepo;
    }

    @GetMapping("/calculate")
    public ResponseEntity<DietResponse> getDietPlan(@RequestParam String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int calories = dietCalculator.calculateDailyCalories(user);
        int protein = (int) (calories * 0.30 / 4);
        int carbs = (int) (calories * 0.40 / 4);
        int fats = (int) (calories * 0.30 / 9);

        return ResponseEntity.ok(new DietResponse(calories, protein, carbs, fats));
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateAiDietPlan(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int targetCalories = dietCalculator.calculateDailyCalories(user);
        String aiMealPlan = aiService.generateDietPlan(user, targetCalories);
        return ResponseEntity.ok(aiMealPlan);
    }
    @GetMapping("/history")
    public ResponseEntity<?> getDietHistory(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(dietPlanRepo.findByUserIdOrderByCreatedAtDesc(user.getId()));
    }
}