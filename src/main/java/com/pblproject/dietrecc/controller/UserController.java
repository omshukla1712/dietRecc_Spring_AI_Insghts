package com.pblproject.dietrecc.controller;

import com.pblproject.dietrecc.dto.UserDTO;
import com.pblproject.dietrecc.mapper.UserMapper;
import com.pblproject.dietrecc.model.User;
import com.pblproject.dietrecc.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@RequestParam String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(userMapper.toDto(user));
    }
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserDTO updatedData) {
        User user = userRepo.findByUsername(updatedData.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUserFromDto(updatedData, user);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully!"));
    }
}
