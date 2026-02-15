package com.pblproject.dietrecc.service;

import com.pblproject.dietrecc.model.User;
import org.springframework.stereotype.Service;

@Service
public class DietCalculatorService {

    public int calculateDailyCalories(User user) {
        double bmr = (10 * user.getWeight())
                + (6.25 * user.getHeight())
                - (5 * user.getAge());

        if (user.getGender() == User.Gender.MALE) {
            bmr += 5;
        } else {
            bmr -= 161;
        }

        double tdee = bmr * getActivityMultiplier(user.getActivityLevel());

        int finalCalories = (int) tdee;

        switch (user.getGoal()) {
            case LOSE_WEIGHT -> finalCalories -= 500;
            case GAIN_MUSCLE -> finalCalories += 300;
        }

        return finalCalories;
    }

    private double getActivityMultiplier(User.ActivityLevel level) {
        return switch (level) {
            case SEDENTARY -> 1.2;
            case LIGHTLY_ACTIVE -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case VERY_ACTIVE -> 1.725;
            case EXTRA_ACTIVE -> 1.9;
        };
    }
}