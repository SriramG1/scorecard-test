package com.test.score.scheduler.config;

import com.test.score.scheduler.entity.User;
import com.test.score.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRole(User.Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Admin user created: admin / admin123");
        }

        // Create demo user if not exists
        if (!userRepository.existsByUsername("demo")) {
            User demo = new User();
            demo.setUsername("demo");
            demo.setEmail("demo@example.com");
            demo.setPassword(passwordEncoder.encode("demo123"));
            demo.setFirstName("Demo");
            demo.setLastName("User");
            demo.setRole(User.Role.USER);
            demo.setEnabled(true);
            userRepository.save(demo);
            System.out.println("Demo user created: demo / demo123");
        }
    }
}

