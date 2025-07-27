package com.example.todo_service.service;

import com.example.todo_service.model.Task;
import com.example.todo_service.model.User;
import com.example.todo_service.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task, Long userId) {
        task.setUserId(userId);
        Task saved = taskRepository.save(task);
        return saved;
    }

    public List<Task> getTasks(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }
}
