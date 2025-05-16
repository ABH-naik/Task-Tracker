package com.tasktracker.dto.response;

import com.tasktracker.model.entity.Task;
import com.tasktracker.model.enums.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String description,
        LocalDate dueDate,
        TaskStatus status,
        Long projectId,
        String projectName,
        Long ownerId,
        String ownerName,
        LocalDateTime createdAt,
        Long assigneeId
) {
    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getOwner().getId(),
                task.getOwner().getName(),
                task.getCreatedAt(),
                task.getAssignee() != null ? task.getAssignee().getId() : null
        );
    }
}
