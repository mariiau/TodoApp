package com.example.todoApp.controller;

import com.example.todoApp.model.Task;
import com.example.todoApp.model.User;
import com.example.todoApp.repository.TaskRepository;
import com.example.todoApp.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TaskController {

    private final TaskRepository taskRepository;
    private final TokenService tokenService;

    public TaskController(TaskRepository taskRepository, TokenService tokenService) {
        this.taskRepository = taskRepository;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestHeader("Authorization") String token,
                                        @RequestBody Task requestTask) {
        User user = tokenService.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        Task task = new Task();
        task.setDescription(requestTask.getDescription());
        task.setDueDate(requestTask.getDueDate());
        task.setStatus(requestTask.getStatus());
        task.setTitle(requestTask.getTitle());
        task.setUser(user);
        taskRepository.save(task);

        return ResponseEntity.ok("Todo created");
    }

    @GetMapping
    public ResponseEntity<?> getTodos(@RequestHeader("Authorization") String token) {
        User user = tokenService.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        List<Task> todos = taskRepository.findAllByUser(user);
        return ResponseEntity.ok(todos);
    }
}
