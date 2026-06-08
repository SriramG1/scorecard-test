package com.test.score.scheduler.service;

import com.test.score.scheduler.entity.Project;
import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> findUserProjects(User user) {
        return projectRepository.findAllUserProjects(user);
    }

    public List<Project> findByOwner(User owner) {
        return projectRepository.findByOwner(owner);
    }

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public long countUserProjects(User user) {
        return projectRepository.countUserProjects(user);
    }

    public Project addMember(Long projectId, User member) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.getMembers().add(member);
        return projectRepository.save(project);
    }

    public Project removeMember(Long projectId, User member) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.getMembers().remove(member);
        return projectRepository.save(project);
    }
}

