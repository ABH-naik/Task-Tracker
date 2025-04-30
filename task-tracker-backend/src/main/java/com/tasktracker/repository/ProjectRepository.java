package com.tasktracker.repository;

import com.tasktracker.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find projects with tasks and owner (eager loading)
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks LEFT JOIN FETCH p.owner WHERE p.id = :id")
    Optional<Project> findProjectWithDetails(@Param("id") Long id);

    // Find projects by owner's OAuth provider ID
    @Query("SELECT p FROM Project p JOIN p.owner u WHERE u.oauthProviderId = :providerId")
    List<Project> findByOwnerProviderId(@Param("providerId") String providerId);
}