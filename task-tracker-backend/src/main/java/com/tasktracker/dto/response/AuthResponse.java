package com.tasktracker.dto.response;

import com.tasktracker.model.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse{
    private Long id; // User ID
    private String email; // User's email
    private String name; // User's name
    private RoleType role; // User's role in the application
    private Boolean emailVerified;
    Instant tokenExpiry;//for frontend
}