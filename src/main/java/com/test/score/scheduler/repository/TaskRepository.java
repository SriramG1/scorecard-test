package com.test.score.scheduler.repository;

import com.test.score.scheduler.entity.Task;
import com.test.score.scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(User user);
    List<Task> findByCreatedBy(User user);
    List<Task> findByStatus(Task.Status status);
    List<Task> findByAssignedToAndStatus(User user, Task.Status status);
    
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user OR t.createdBy = :user ORDER BY t.createdAt DESC")
    List<Task> findAllUserTasks(@Param("user") User user);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = :user AND t.status = :status")
    long countByAssignedToAndStatus(@Param("user") User user, @Param("status") Task.Status status);
}

