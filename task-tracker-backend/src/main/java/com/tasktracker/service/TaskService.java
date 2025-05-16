package com.tasktracker.service;

import com.tasktracker.dto.response.TaskResponse;
import com.tasktracker.exception.OwnerNotFoundException;
import com.tasktracker.exception.ProjectNotFoundException;
import com.tasktracker.exception.TaskNotFoundException;
import com.tasktracker.model.entity.Project;
import com.tasktracker.model.entity.Task;
import com.tasktracker.model.entity.User;
import com.tasktracker.model.enums.TaskStatus;
import com.tasktracker.repository.ProjectRepository;
import com.tasktracker.repository.TaskRepository;
import com.tasktracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + projectId));

        Task task = Task.builder()
                .description(description)
                .dueDate(dueDate)
                .status(TaskStatus.NOT_STARTED)
                .owner(owner)
                .project(project)
                .build();

        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
    }

        Task save = taskRepository.save(task);
        return TaskResponse.fromEntity(save);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, TaskStatus status, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Project not found with ID: "+taskId));
        task.setStatus(status);
        if (assigneeId != null) {
            User newAssignee = userRepository.findById(assigneeId).orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignee(newAssignee);
        }

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
    @Transactional
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteTask(taskId);
    }
    public List<Task> getAllTasksForProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }
}
