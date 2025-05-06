package com.tasktracker.service;

import com.tasktracker.dto.request.ProjectRequest;
import com.tasktracker.dto.response.ProjectResponse;
import com.tasktracker.exception.OwnerNotFoundException;
import com.tasktracker.exception.ProjectNotFoundException;
import com.tasktracker.model.entity.Project;
import com.tasktracker.model.entity.User;
import com.tasktracker.repository.ProjectRepository;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found with ID: " + ownerId));

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
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " +projectId));
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByOwner(String oauthProviderId) {
        return projectRepository.findByOwnerProviderId(oauthProviderId);
    }
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(ProjectResponse::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public Project updateProject(Long projectId, ProjectRequest projectRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " +projectId));

        // Update project fields based on the request
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStartDate(projectRequest.getStartDate());
        project.setEndDate(projectRequest.getEndDate());

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found with ID: " +projectId);
        }
        projectRepository.deleteById(projectId);
    }




}