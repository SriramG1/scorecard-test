package com.test.score.scheduler.controller;

import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", User.Role.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                @RequestParam User.Role role,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "User role updated successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "User status updated successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/users";
    }
}

