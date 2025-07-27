package com.example.auth_service;

import com.example.auth_service.repository.TaskRepository;
import com.example.auth_service.model.LoginRequest;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.TaskRepository;
import com.example.auth_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDb() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_newUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User registered")));
    }
    @Test
    void register_existingUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username already exists")));
    }

    @Test
    void verifyToken_validToken() throws Exception {
        User user = new User("user", "password0303");
        user = userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());

        String token = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(token);

        mockMvc.perform(post("/token")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void verifyToken_invalidToken() throws Exception {
        mockMvc.perform(post("/token")
                        .header("Authorization", "Invalid token"))
                .andExpect(status().isUnauthorized());
    }

}
