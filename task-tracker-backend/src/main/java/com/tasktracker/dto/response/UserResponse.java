package com.tasktracker.dto.response;

import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.RoleType;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        RoleType role,
        LocalDateTime createdAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}