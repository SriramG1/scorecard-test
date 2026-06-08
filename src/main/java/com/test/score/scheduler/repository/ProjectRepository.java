package com.test.score.scheduler.repository;

import com.test.score.scheduler.entity.Project;
import com.test.score.scheduler.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
    List<Project> findByStatus(Project.Status status);

    @Query("SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members")
    List<Project> findAllUserProjects(@Param("user") User user);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members")
    long countUserProjects(@Param("user") User user);
}

