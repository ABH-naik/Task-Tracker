package com.tasktracker.service;

import com.tasktracker.model.entity.Project;
import com.tasktracker.model.entity.User;
import com.tasktracker.repository.ProjectRepository;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public Project createProject(String name, String description, Long ownerId, LocalDate startDate, LocalDate endDate) {

        // Validate inputs
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Project description is required");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Project project = Project.builder()
                .name(name)
                .description(description)
                .owner(owner)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project getProjectWithDetails(Long projectId) {
        return projectRepository.findProjectWithDetails(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByOwner(String oauthProviderId) {
        return projectRepository.findByOwnerProviderId(oauthProviderId);
    }
}