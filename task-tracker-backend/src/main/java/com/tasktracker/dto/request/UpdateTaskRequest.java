package com.tasktracker.dto.request;

import com.tasktracker.model.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank(message = "Description is required") String description,
        @FutureOrPresent(message = "Due date cannot be in the past") LocalDate dueDate,
        @NotNull(message = "Status is required") TaskStatus status,
        Long assigneeId  // optional
) {}