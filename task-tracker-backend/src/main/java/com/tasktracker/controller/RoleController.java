package com.tasktracker.controller;

import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import com.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:3000")   // your React app origin
public class RoleController {

    @Autowired
    private UserRepository userRepos;

    @GetMapping
    public List<String> getAllRoles() {
        return Arrays.stream(RoleType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
    @PutMapping("/user/{userId}")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {

        String roleName = body.get("role");
        RoleType newRole = RoleType.valueOf(roleName);

        User user = userRepos.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(newRole);
        userRepos.save(user);

        return ResponseEntity.noContent().build();
    }
}