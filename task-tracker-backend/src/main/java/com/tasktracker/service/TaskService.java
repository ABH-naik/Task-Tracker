package com.tasktracker.service;

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
    public Task createTask(String description, LocalDate dueDate, Long projectId, Long ownerId) {
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
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Task task = Task.builder()
                .description(description)
                .dueDate(dueDate)
                .status(TaskStatus.NOT_STARTED)
                .owner(owner)
                .project(project)
                .build();

        return taskRepository.save(task);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);

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
}