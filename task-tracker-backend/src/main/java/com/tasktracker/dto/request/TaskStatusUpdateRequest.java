package com.tasktracker.dto.request;

import com.tasktracker.model.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class TaskStatusUpdateRequest {
    @NotNull(message = "User ID is required")
    Long userId;
    @NotNull(message = "Project ID is required")
    Long projectId;
    @NotNull(message = "Task ID is required")
    Long taskId;
    @NotNull(message = "Task status is required")
    TaskStatus status;
}
// This record is used to update the status of a task. It contains the user ID, project ID, task ID, and the new status.