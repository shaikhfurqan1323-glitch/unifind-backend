package com.unifind.backend.controller;

import com.unifind.backend.model.User;
import com.unifind.backend.repository.UserRepository;
import com.unifind.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByStudentId(user.getStudentId()).isPresent()) {
            return ResponseEntity.badRequest().body("Student ID already registered!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use!");
        }

        user.setRole(User.Role.STUDENT);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        // Ab Student ID aur Password aayega
        String studentId = request.get("studentId");
        String password = request.get("password");

        Optional<User> userOpt = userRepository.findByStudentId(studentId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Student ID not found");
        }

        User user = userOpt.get();
        if (!password.equals(user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("name", user.getFullName());
        response.put("email", user.getEmail()); // Contact ke liye
        response.put("studentId", user.getStudentId());
        response.put("role", user.getRole().name());
        
        return ResponseEntity.ok(response);
    }
}