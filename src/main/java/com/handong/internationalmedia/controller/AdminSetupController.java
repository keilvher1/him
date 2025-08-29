package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.entity.User;
import com.handong.internationalmedia.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin-setup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminSetupController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.setup.secret:#{null}}")
    private String setupSecret;

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminRequest request) {
        try {
            // 보안 검증: 설정된 시크릿 키가 있는지 확인
            if (setupSecret == null || setupSecret.isEmpty()) {
                log.warn("Admin setup attempted but no setup secret configured");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Admin setup is disabled"));
            }
            
            // 시크릿 키 검증
            if (!setupSecret.equals(request.getSetupSecret())) {
                log.warn("Invalid setup secret provided for admin creation");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid setup secret"));
            }
            
            // 기존 관리자가 있는지 확인
            if (userService.hasAdminUsers()) {
                log.warn("Admin setup attempted but admin users already exist");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Admin users already exist"));
            }
            
            // 입력 검증
            if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().length() < 8 ||
                request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid input data"));
            }
            
            // 관리자 계정 생성
            User admin = User.builder()
                    .username(request.getUsername().trim())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail().trim())
                    .fullName(request.getFullName())
                    .role(User.Role.ADMIN)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            userService.save(admin);
            log.info("Admin user created successfully: {}", admin.getUsername());
            
            return ResponseEntity.ok(Map.of(
                "message", "Admin user created successfully",
                "username", admin.getUsername()
            ));
            
        } catch (Exception e) {
            log.error("Error creating admin user", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create admin user"));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getSetupStatus() {
        boolean hasAdmins = userService.hasAdminUsers();
        boolean setupEnabled = setupSecret != null && !setupSecret.isEmpty();
        
        return ResponseEntity.ok(Map.of(
            "setupAvailable", setupEnabled && !hasAdmins,
            "hasAdminUsers", hasAdmins
        ));
    }
    
    public static class CreateAdminRequest {
        private String setupSecret;
        private String username;
        private String password;
        private String email;
        private String fullName;
        
        // Getters and setters
        public String getSetupSecret() { return setupSecret; }
        public void setSetupSecret(String setupSecret) { this.setupSecret = setupSecret; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }
}