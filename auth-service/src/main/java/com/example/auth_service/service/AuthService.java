package com.example.auth_service.service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void register(String username, String password) {
        User user = new User(username, password);
        userRepository.save(user);
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return null;
        }
        return tokenService.generateToken(userOpt.get());
    }

    public boolean isTokenValid(String token) {
        return tokenService.isValid(token);
    }

    public User getUserFromToken(String token) {
        return tokenService.getUserFromToken(token);
    }

    public Optional<User> getUserFromUsername(String username){
        return userRepository.findByUsername(username);
    }
}