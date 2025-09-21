package com.tasktracker.controller;

import com.tasktracker.dto.request.TaskRequest;
import com.tasktracker.dto.request.UpdateTaskRequest;
import com.tasktracker.dto.response.TaskResponse;
import com.tasktracker.model.entity.Task;
import com.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(
                taskRequest.description(),
                taskRequest.dueDate(),
                taskRequest.projectId(),
                taskRequest.ownerId(),
                taskRequest.assigneeId()
        );
        return ResponseEntity.status(201).body(taskResponse);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<Task> overdueTasks = taskService.getOverdueTasks();
        List<TaskResponse> taskResponses = overdueTasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getAllTasksForProject(projectId);
        List<TaskResponse> taskResponses = tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponse>> getTasksByUserId(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByUserId(userId);
        List<TaskResponse> taskResponses = tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/owner/{oauthProviderId}")
    public ResponseEntity<List<TaskResponse>> getTasksByOwner(@PathVariable String oauthProviderId) {
        List<Task> tasks = taskService.getTasksByOwner(oauthProviderId);
        List<TaskResponse> taskResponses = tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskResponse>> getTasksAssignedToUser(
            @RequestParam Long userId,
            @RequestParam Long projectId) {
        List<Task> tasks = taskService.getTasksByUserAndProject(userId, projectId);
        List<TaskResponse> response = tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest req) {
        TaskResponse updated = taskService.updateTask(id, req);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam Long projectId,
            @RequestParam String status) {
        TaskResponse updated = taskService.updateTaskStatus(id, userId, projectId, status);
        return ResponseEntity.ok(updated);
    }
}