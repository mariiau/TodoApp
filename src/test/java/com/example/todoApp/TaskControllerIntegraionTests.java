package com.example.todoApp;

import com.example.todoApp.model.LoginRequest;
import com.example.todoApp.model.Task;
import com.example.todoApp.model.User;
import com.example.todoApp.repository.TaskRepository;
import com.example.todoApp.repository.UserRepository;
import com.example.todoApp.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegraionTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TokenService tokenService;

    private String token;
    private User user;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("user", "password");
        user = userRepository.save(user);

        token = tokenService.generateToken(user);
    }

    @Test
    void getTasks() throws Exception {
        Task task = new Task();
        task.setTitle("My Task");
        task.setUser(user);
        task.setStatus(Task.Status.TODO);
        taskRepository.save(task);

        mockMvc.perform(get("/todos")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("My Task"))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }
    @Test
    void getTasks_anotherUser() throws Exception {
        User other = new User("Max", "secret");
        userRepository.save(other);

        mockMvc.perform(get("/todos")
                        .param("username", "Max")
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasks_withoutToken() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isUnauthorized());
    }

}
