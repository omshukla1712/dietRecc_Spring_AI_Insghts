package com.pblproject.dietrecc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DietResponse {
    private int dailyCalories;
    private int proteinGrams;
    private int carbGrams;
    private int fatGrams;
}