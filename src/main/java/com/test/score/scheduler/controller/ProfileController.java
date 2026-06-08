package com.test.score.scheduler.controller;

import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect");
            return "profile/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "profile/change-password";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            return "profile/change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/profile";
    }
}

