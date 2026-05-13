package com.example.backend.Config;

import com.example.backend.entity.Role;
import com.example.backend.entity.Users;
import com.example.backend.repository.UsersRepo;
import com.example.backend.entity.Category;
import com.example.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsersRepo usersRepo;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@vshop.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123@Password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // 1. Tạo Category mặc định nếu chưa có
        if (categoryRepository.count() == 0) {
            Category defaultCategory = Category.builder()
                    .name("General")
                    .description("Default category")
                    .build();
            categoryRepository.save(defaultCategory);
        }

        // 2. Kiểm tra và tạo tài khoản ADMIN
        if (usersRepo.findAll().stream().noneMatch(u -> u.getRole() == Role.ROLE_ADMIN)) {
            Users admin = Users.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .emailVerified(true)
                    .createdAt(LocalDateTime.now())
                    .profileImageUrl("https://via.placeholder.com/150")
                    .build();

            usersRepo.save(admin);
        }
    }
}
