package com.test.score.scheduler.controller;

import com.test.score.scheduler.entity.Project;
import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.service.ProjectService;
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
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public String listProjects(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Project> projects = projectService.findUserProjects(user);
        model.addAttribute("projects", projects);
        model.addAttribute("user", user);
        return "projects/list";
    }

    @GetMapping("/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("statuses", Project.Status.values());
        return "projects/form";
    }

    @PostMapping
    public String createProject(@Valid @ModelAttribute("project") Project project,
                               BindingResult result,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("statuses", Project.Status.values());
            return "projects/form";
        }

        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        project.setOwner(currentUser);

        projectService.createProject(project);
        redirectAttributes.addFlashAttribute("success", "Project created successfully!");
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        Project project = projectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        model.addAttribute("users", userService.findAllUsers());
        return "projects/view";
    }

    @GetMapping("/{id}/edit")
    public String editProjectForm(@PathVariable Long id, Model model) {
        Project project = projectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("statuses", Project.Status.values());
        return "projects/form";
    }

    @PostMapping("/{id}")
    public String updateProject(@PathVariable Long id,
                               @Valid @ModelAttribute("project") Project project,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("statuses", Project.Status.values());
            return "projects/form";
        }

        Project existingProject = projectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        existingProject.setStartDate(project.getStartDate());
        existingProject.setEndDate(project.getEndDate());
        existingProject.setStatus(project.getStatus());

        projectService.updateProject(existingProject);
        redirectAttributes.addFlashAttribute("success", "Project updated successfully!");
        return "redirect:/projects";
    }

    @PostMapping("/{id}/members/add")
    public String addMember(@PathVariable Long id,
                           @RequestParam Long userId,
                           RedirectAttributes redirectAttributes) {
        User member = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        projectService.addMember(id, member);
        redirectAttributes.addFlashAttribute("success", "Member added successfully!");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/members/{userId}/remove")
    public String removeMember(@PathVariable Long id,
                              @PathVariable Long userId,
                              RedirectAttributes redirectAttributes) {
        User member = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        projectService.removeMember(id, member);
        redirectAttributes.addFlashAttribute("success", "Member removed successfully!");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute("success", "Project deleted successfully!");
        return "redirect:/projects";
    }
}

