package com.example.todo_service;

import com.example.todo_service.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegraionTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    private String tokenUser1;
    private String tokenUser2;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();

        tokenUser1 = registerAndLogin("user1", "password1");
        tokenUser2 = registerAndLogin("user2", "password2");
    }

    private String registerAndLogin(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> userData = Map.of("username", username, "password", password);

        try {
            restTemplate.postForEntity(authServiceUrl + "/register", userData, Void.class);
        } catch (HttpClientErrorException.BadRequest e) {
            if (!e.getResponseBodyAsString().contains("Username already exists")) {
                throw e;
            }
        }

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                authServiceUrl + "/login", userData, String.class);

        String token = loginResponse.getBody();
        return token;
    }

    @Test
    void getTasks() throws Exception {
        Map<String, Object> task = Map.of(
                "title", "My Task",
                "status", "TODO"
        );

        mockMvc.perform(post("/todos")
                        .header("Authorization", tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/todos")
                        .header("Authorization", tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("My Task"))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }


    @Test
    void getTasks_anotherUser() throws Exception {
        mockMvc.perform(get("/todos")
                        .header("Authorization", tokenUser2)
                        .param("username", "user1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasks_withoutToken() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isUnauthorized());
    }

}
