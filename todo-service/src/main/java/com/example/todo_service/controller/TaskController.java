package com.example.todo_service.controller;

import com.example.todo_service.model.Task;
import com.example.todo_service.model.User;
import com.example.todo_service.service.AuthService;
import com.example.todo_service.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TaskController {

    private final TaskService taskService;
    private AuthService authService;

    public TaskController(TaskService taskService, AuthService authService) {
        this.taskService = taskService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createTasks(@RequestHeader("Authorization") String token,
                                         @RequestBody Task taskRequest) {
        User user = authService.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Task saved = taskService.createTask(taskRequest, user.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestParam(required = false) String username) {
        if (token != null) {
            User userToken = authService.getUserFromToken(token);
            if (userToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            if (username != null) {
                if (!Objects.equals(userToken.getUsername(), username)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Token is valid but user is trying to access data they shouldn’t");
                }
            }
            List<Task> tasks = taskService.getTasks(userToken.getId());
            return ResponseEntity.ok(tasks);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

    }
}