package com.test.score.scheduler.controller;

import com.test.score.scheduler.entity.Task;
import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.service.ProjectService;
import com.test.score.scheduler.service.TaskService;
import com.test.score.scheduler.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final TaskService taskService;
    private final ProjectService projectService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalTasks = taskService.findUserTasks(user).size();
        long completedTasks = taskService.countByStatus(user, Task.Status.COMPLETED);
        long pendingTasks = taskService.countByStatus(user, Task.Status.TODO);
        long inProgressTasks = taskService.countByStatus(user, Task.Status.IN_PROGRESS);
        long totalProjects = projectService.countUserProjects(user);

        model.addAttribute("user", user);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("inProgressTasks", inProgressTasks);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("recentTasks", taskService.findUserTasks(user).stream().limit(5).toList());
        model.addAttribute("recentProjects", projectService.findUserProjects(user).stream().limit(5).toList());

        return "dashboard";
    }
}

