package com.tasktracker.controller;


import com.tasktracker.dto.request.ProjectRequest;
import com.tasktracker.dto.response.ProjectResponse;
import com.tasktracker.model.entity.Project;
import com.tasktracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest) {
        Project project = projectService.createProject(
                projectRequest.getName(),
                projectRequest.getDescription(),
                projectRequest.getOwnerId(),
                projectRequest.getStartDate(),
                projectRequest.getEndDate()
        );
        ProjectResponse projectResponse = ProjectResponse.fromEntity(project);
        return ResponseEntity.status(201).body(projectResponse);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectWithDetails(id);
        ProjectResponse projectResponse = ProjectResponse.fromEntity(project);
        return ResponseEntity.ok(projectResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectRequest projectRequest) {
        Project project = projectService.updateProject(id, projectRequest);
        ProjectResponse projectResponse = ProjectResponse.fromEntity(project);
        return ResponseEntity.ok(projectResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}

