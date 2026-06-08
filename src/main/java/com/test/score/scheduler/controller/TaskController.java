package com.test.score.scheduler.controller;

import com.test.score.scheduler.entity.Task;
import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.service.TaskService;
import com.test.score.scheduler.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String listTasks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Task> tasks = taskService.findUserTasks(user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTaskForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/form";
    }

    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task,
                            BindingResult result,
                            @RequestParam(required = false) Long assignedToId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("priorities", Task.Priority.values());
            return "tasks/form";
        }

        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setCreatedBy(currentUser);

        if (assignedToId != null) {
            userService.findById(assignedToId).ifPresent(task::setAssignedTo);
        } else {
            task.setAssignedTo(currentUser);
        }

        taskService.createTask(task);
        redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id,
                            @Valid @ModelAttribute("task") Task task,
                            BindingResult result,
                            @RequestParam(required = false) Long assignedToId,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("statuses", Task.Status.values());
            model.addAttribute("priorities", Task.Priority.values());
            return "tasks/form";
        }

        Task existingTask = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());
        existingTask.setDueDate(task.getDueDate());

        if (assignedToId != null) {
            userService.findById(assignedToId).ifPresent(existingTask::setAssignedTo);
        }

        taskService.updateTask(existingTask);
        redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                              @RequestParam Task.Status status,
                              RedirectAttributes redirectAttributes) {
        taskService.updateTaskStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Task status updated!");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        return "redirect:/tasks";
    }
}

