package com.test.score.scheduler.service;

import com.test.score.scheduler.entity.Task;
import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> findUserTasks(User user) {
        return taskRepository.findAllUserTasks(user);
    }

    public List<Task> findByAssignedTo(User user) {
        return taskRepository.findByAssignedTo(user);
    }

    public List<Task> findByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public long countByStatus(User user, Task.Status status) {
        return taskRepository.countByAssignedToAndStatus(user, status);
    }

    public Task updateTaskStatus(Long taskId, Task.Status status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }
}

