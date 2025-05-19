package com.tasktracker.controller;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@JsonPropertyOrder({"jwt", "userId", "isAdmin", "isTaskCreator", "readonly", "expiration", "isError"})
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepos;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String email) {
        User user = userRepos.findByEmail(email).orElse(null);

        if (user == null) {
            Map<String, Object> errorResponse = Map.of(
                    "isError", true,
                    "message", "User not found"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        String token = userService.generateJwtToken(user);
        long expirationTime = System.currentTimeMillis() + 3600000;
        String expirationIST = Instant.ofEpochMilli(expirationTime)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> response = Map.of(
                "userId", user.getId(), // Include userId in the response
                "isAdmin", user.getRole() == RoleType.ADMIN,
                "readonly", user.getRole() == RoleType.READ_ONLY_USER,
                "isTaskCreator", user.getRole() == RoleType.TASK_CREATOR,
                "isError", false,
                "jwt", token,
                "expiration", expirationIST
        );

        return ResponseEntity.ok(response);
    }
}
