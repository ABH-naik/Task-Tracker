package com.tasktracker.controller;


import com.tasktracker.dto.request.TaskRequest;
import com.tasktracker.dto.response.TaskResponse;
import com.tasktracker.model.entity.Task;
import com.tasktracker.model.enums.TaskStatus;
import com.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.tasktracker.dto.request.TaskRequest;
import com.tasktracker.dto.response.TaskResponse;
import com.tasktracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TASK_CREATOR')")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(
                taskRequest.description(),
                taskRequest.dueDate(),
                taskRequest.projectId(),
                taskRequest.ownerId()
        );
        return ResponseEntity.status(201).body(taskResponse);
    }




    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        taskService.updateTaskStatus(id, status);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<Task> overdueTasks = taskService.getOverdueTasks(); // Get the list of overdue tasks
        List<TaskResponse> taskResponses = overdueTasks.stream() // Convert List<Task> to List<TaskResponse>
                .map(TaskResponse::fromEntity) // Use the fromEntity method to convert each Task to TaskResponse
                .collect(Collectors.toList()); // Collect the results into a List<TaskResponse>

        return ResponseEntity.ok(taskResponses); // Return the response
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getAllTasksForProject(projectId);
        List<TaskResponse> taskResponses = tasks.stream().map(TaskResponse::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(taskResponses);
    }



    @GetMapping("/owner/{oauthProviderId}")
    public ResponseEntity<List<TaskResponse>> getTasksByOwner(@PathVariable String oauthProviderId) {
        List<Task> tasks = taskService.getTasksByOwner(oauthProviderId); // Get the list of tasks by owner
        List<TaskResponse> taskResponses = tasks.stream() // Convert List<Task> to List<TaskResponse>
                .map(TaskResponse::fromEntity) // Use the fromEntity method to convert each Task to TaskResponse
                .collect(Collectors.toList()); // Collect the results into a List<TaskResponse>

        return ResponseEntity.ok(taskResponses); // Return the response
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("@taskSecurity.isTaskOwner(#id) or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }
}
