package com.tasktracker.dto.request;

import com.tasktracker.model.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public record TaskRequest(
        @NotBlank String description,
        @FutureOrPresent LocalDate dueDate,
        @NotNull Long projectId,
        @NotNull Long ownerId, // Reference to the user assigned to the task
        Long assigneeId  // Optional (no @NotNull annotation)

) {}