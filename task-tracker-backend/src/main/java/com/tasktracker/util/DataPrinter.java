package com.tasktracker.util;

import com.tasktracker.repository.UserRepository;
import com.tasktracker.repository.ProjectRepository;
import com.tasktracker.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class DataPrinter {

    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;
    private final TaskRepository taskRepo;

    public DataPrinter(UserRepository userRepo, ProjectRepository projectRepo, TaskRepository taskRepo) {
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }

    @Transactional
    public void printData() {
        System.out.println("== Users and their Projects ==");
        userRepo.findAll().forEach(user -> {
            System.out.println("UserName: " + user.getName()+"UserGoogleId: " +user.getOauthProviderId()+"UserEmail: "+user.getEmail());
            user.getProjects().forEach(project -> {
                System.out.println("   -> Project: " + project.getName());
            });
        });

        System.out.println("== Projects ==");
        projectRepo.findAll().forEach(p -> {
            System.out.println(p.getName() + " owned by " + p.getOwner().getEmail());
        });

        System.out.println("== Tasks ==");
        taskRepo.findAll().forEach(t -> {
            System.out.println(t.getDescription() + " for project " + t.getProject().getName() + ", assigned to " + t.getOwner().getEmail());
        });
    }
}
