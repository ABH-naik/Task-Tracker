package com.tasktracker.controller;

import com.tasktracker.dto.request.OAuthLoginRequest;
import com.tasktracker.dto.request.UserRequest;
import com.tasktracker.dto.response.AuthResponse;
import com.tasktracker.dto.response.UserResponse;
import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Create or login via OAuth
    @PostMapping("/oauth/login")
    public ResponseEntity<AuthResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        User user = userService.findOrCreateUser(request.oauthProviderId(), request.email(), request.name());
        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                "mocked-token" // Replace with JWT/actual token generation if implemented
        );
        return ResponseEntity.ok(response);
    }

    // 2. Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    // 3. Update user role
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long id, @RequestParam RoleType role) {
        User updatedUser = userService.updateUserRole(id, role);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    // 4. Get users by role
    @GetMapping("/by-role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam RoleType role) {
        List<User> users = userService.getUsersByRole(role);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
