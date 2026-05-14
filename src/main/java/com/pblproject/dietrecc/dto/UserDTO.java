package com.pblproject.dietrecc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private int age;
    private String gender;
    private double weight;
    private double height;
    private String activityLevel;
    private String goal;
}
