package com.pblproject.dietrecc.service;

import com.pblproject.dietrecc.model.User;
import com.pblproject.dietrecc.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    public User registerUser(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepo.save(user);
    }
}
