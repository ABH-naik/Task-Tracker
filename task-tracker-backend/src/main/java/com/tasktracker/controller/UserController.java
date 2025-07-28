package com.tasktracker.controller;

import com.tasktracker.dto.request.OAuthLoginRequest;
import com.tasktracker.dto.request.UserRequest;
import com.tasktracker.dto.response.AuthResponse;
import com.tasktracker.dto.response.ProjectResponse;
import com.tasktracker.dto.response.UserResponse;
import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest userRequest) {
        User createdUser = userService.findOrCreateUser(
                userRequest.getEmail(),
                userRequest.getName(),
                userRequest.getOauthProviderId(),
                userRequest.isEmailVerified()
        );
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can delete users
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        String responseMessage = "User deleted with ID " + userId;
        return ResponseEntity.ok(responseMessage);
    }
    // 2. Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
    @GetMapping(path = { "", "/" })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> projects = userService.getAllUsers();
        return ResponseEntity.ok(projects);
    }

    // 3. Update user role
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        RoleType roleType;
        try {
            roleType = RoleType.fromString(role);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(null); // or send custom error object
        }
        User updatedUser = userService.updateUserRole(id, roleType);
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