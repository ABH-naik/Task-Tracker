package com.tasktracker.controller;

import com.tasktracker.model.entity.User;
import com.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email) {
        // Check if the user exists
        User user = userService.getUserByEmail(email);

        // Generate JWT token with role
        String token = userService.generateJwtToken(user);

        return ResponseEntity.ok(token);
    }
}
