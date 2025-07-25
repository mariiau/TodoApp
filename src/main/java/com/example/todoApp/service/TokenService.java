package com.example.todoApp.service;

import com.example.todoApp.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {
    private final Map<String, User> tokenMap = new HashMap<>();

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, user);
        return token;
    }

    public User getUserFromToken(String token) {
        return tokenMap.get(token);
    }

    public boolean isValid(String token) {
        return tokenMap.containsKey(token);
    }
}
