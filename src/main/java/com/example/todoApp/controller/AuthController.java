package com.example.todoApp.controller;

import com.example.todoApp.service.AuthService;
import com.example.todoApp.model.LoginRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        if (authService.usernameExists(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        if (token == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        if (authService.isTokenValid(token)) {
            return ResponseEntity.ok("Valid token");
        } else {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
}
