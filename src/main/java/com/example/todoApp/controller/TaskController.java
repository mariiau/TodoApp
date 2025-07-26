package com.example.todoApp.controller;

import com.example.todoApp.model.Task;
import com.example.todoApp.model.User;
import com.example.todoApp.service.AuthService;
import com.example.todoApp.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TaskController {

    private final TaskService taskService;
    private final AuthService authService;

    public TaskController(TaskService taskService, AuthService authService) {
        this.taskService = taskService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createTasks(@RequestHeader("Authorization") String token,
                                        @RequestBody Task taskRequest) {
        User user = authService.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        Task saved = taskService.createTask(taskRequest, user);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader("Authorization") String token,
                                      @RequestParam(required = false) String username) {
        User userToken = authService.getUserFromToken(token);
        if (userToken == null) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        if (username != null) {
            Optional<User> requestedUser = authService.getUserFromUsername(username);

            if (requestedUser.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            if (!Objects.equals(userToken.getId(), requestedUser.get().getId())) {
                return ResponseEntity.status(403).body("Token is valid but the user is trying to access data they shouldn’t");
            }
        }
        List<Task> tasks = taskService.getTasks(userToken);
        return ResponseEntity.ok(tasks);
    }
}