package com.handong.internationalmedia.controller;

import com.handong.internationalmedia.dto.LoginRequest;
import com.handong.internationalmedia.dto.RegisterRequest;
import com.handong.internationalmedia.entity.User;
import com.handong.internationalmedia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            User user = (User) authentication.getPrincipal();
            
            return ResponseEntity.ok(new LoginResponse(
                    "Login successful", 
                    user.getUsername(), 
                    user.getFullName(),
                    user.getRole().name(),
                    user.isAdmin()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.createUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getFullName(),
                    User.Role.USER
            );
            
            return ResponseEntity.ok(new MessageResponse("User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(new UserInfo(null, null, null, false));
        }

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(new UserInfo(
                user.getUsername(),
                user.getFullName(),
                user.getRole().name(),
                user.isAdmin()
        ));
    }

    // Response DTOs
    public static class LoginResponse {
        private String message;
        private String username;
        private String fullName;
        private String role;
        private boolean isAdmin;

        public LoginResponse(String message, String username, String fullName, String role, boolean isAdmin) {
            this.message = message;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
            this.isAdmin = isAdmin;
        }

        // Getters
        public String getMessage() { return message; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public boolean isAdmin() { return isAdmin; }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }

    public static class UserInfo {
        private String username;
        private String fullName;
        private String role;
        private boolean isAdmin;

        public UserInfo(String username, String fullName, String role, boolean isAdmin) {
            this.username = username;
            this.fullName = fullName;
            this.role = role;
            this.isAdmin = isAdmin;
        }

        // Getters
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public boolean isAdmin() { return isAdmin; }
    }
}