package com.tasktracker.Security;

import com.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurity {

    private final TaskRepository taskRepository;

    public boolean isTaskOwner(Long taskId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        return taskRepository.findById(taskId)
                .map(task -> task.getOwner().getEmail().equals(currentEmail))
                .orElse(false);
    }
}