package com.tasktracker.dto.response;

import com.tasktracker.model.entity.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Long ownerId,
        String ownerName,
        LocalDateTime createdAt
) {
    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getOwner().getId(),
                project.getOwner().getName(),
                project.getCreatedAt()
        );
    }
}