package com.tasktracker.service;

import com.tasktracker.dto.request.TaskRequest;
import com.tasktracker.dto.request.UpdateTaskRequest;
import com.tasktracker.dto.response.TaskResponse;
import com.tasktracker.exception.ResourceNotFoundException;
import com.tasktracker.model.entity.Project;
import com.tasktracker.model.entity.Task;
import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.TaskStatus;
import com.tasktracker.repository.ProjectRepository;
import com.tasktracker.repository.TaskRepository;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TaskResponse createTask(String description, LocalDate dueDate, Long projectId, Long ownerId, Long assigneeId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description is required");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date is required");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with ID: " + ownerId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        Task task = Task.builder()
                .description(description)
                .dueDate(dueDate)
                .status(TaskStatus.NOT_STARTED)
                .owner(owner)
                .project(project)
                .build();

        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.assigneeId() != null) {
            User assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);
        return TaskResponse.fromEntity(saved);
    }

    // NEW METHOD: This matches the controller endpoint signature
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, Long userId, Long projectId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Validate the task belongs to the specified project
        if (!task.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Task does not belong to the specified project");
        }

        // Validate user has permission to update this task
        // For READ_ONLY users, they can only update status to COMPLETED
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRole().name().equals("READ_ONLY_USER") &&
                !TaskStatus.COMPLETED.name().equals(status)) {
            throw new IllegalArgumentException("Read-only users can only mark tasks as COMPLETED");
        }

        // Update the status
        try {
            TaskStatus newStatus = TaskStatus.valueOf(status.toUpperCase());
            task.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    // Keep the old method for backward compatibility if needed
    public void updateTaskStatus(com.tasktracker.dto.request.TaskStatusUpdateRequest request) {
        Task task = taskRepository.findByIdAndAssigneeIdAndProjectId(
                request.getTaskId(),
                request.getUserId(),
                request.getProjectId()
        ).orElseThrow(() -> new RuntimeException("Task not found for the given user, project, and task ID"));

        task.setStatus(request.getStatus());
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks();
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByOwner(String oauthProviderId) {
        return taskRepository.findByOwnerProviderId(oauthProviderId);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByAssigneeId(userId);
    }

    @Transactional
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteTask(taskId);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasksForProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByUserAndProject(Long userId, Long projectId) {
        return taskRepository.findByAssigneeIdAndProjectId(userId, projectId);
    }
}