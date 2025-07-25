package com.example.todoApp.controller;

import com.example.todoApp.model.User;
import com.example.todoApp.repository.UserRepository;
import com.example.todoApp.service.TokenService;
import com.example.todoApp.model.LoginRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping
public class AuthController {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        //todo when 2 users
        User user = new User(request.username, request.password);
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.username);

        if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(request.password)) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = tokenService.generateToken(optionalUser.get());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        if (tokenService.isValid(token)) {
            return ResponseEntity.ok("Valid token");
        } else {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
}
