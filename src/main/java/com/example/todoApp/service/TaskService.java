package com.example.todoApp.service;

import com.example.todoApp.model.Task;
import com.example.todoApp.model.User;
import com.example.todoApp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    public List<Task> getTasks(User user) {
        return taskRepository.findAllByUser(user);
    }
}
