package com.tasktracker.repository;

import com.tasktracker.model.entity.Task;
import com.tasktracker.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status <> 'COMPLETED'")
    List<Task> findOverdueTasks();

    // Bulk status update
    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id IN :taskIds")
    int updateTaskStatus(@Param("taskIds") List<Long> taskIds, @Param("status") TaskStatus status);

    // Find tasks by owner's OAuth provider ID
    @Query("SELECT t FROM Task t JOIN t.owner u WHERE u.oauthProviderId = :providerId")
    List<Task> findByOwnerProviderId(@Param("providerId") String providerId);
}