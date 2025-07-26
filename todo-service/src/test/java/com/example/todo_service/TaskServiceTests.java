package com.example.todoApp;

import com.example.todoApp.model.Task;
import com.example.todoApp.model.User;
import com.example.todoApp.repository.TaskRepository;
import com.example.todoApp.repository.UserRepository;
import com.example.todoApp.service.AuthService;
import com.example.todoApp.service.TaskService;
import com.example.todoApp.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTodo_savesTodoWithUser() {
        User user = new User("user", "password");
        Task task = new Task();
        task.setTitle("Test");
        task.setDescription("This is task description.");
        task.setDueDate(LocalDate.now().plusDays(5));
        task.setStatus(Task.Status.TODO);

        Task savedTask = new Task();
        savedTask.setId(1);
        savedTask.setTitle(task.getTitle());
        savedTask.setDescription(task.getDescription());
        savedTask.setDueDate(task.getDueDate());
        savedTask.setStatus(task.getStatus());
        savedTask.setUser(user);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        Task result = taskService.createTask(task, user);
        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(taskRepository).save(task);
    }

    @Test
    void getTodos_returnsAllTodosForUser() {
        User user = new User("user", "password");
        Task task1 = new Task();
        task1.setTitle("Test");
        task1.setStatus(Task.Status.TODO);
        task1.setUser(user);

        Task task2 = new Task();
        task2.setTitle("Test 2");
        task1.setStatus(Task.Status.IN_PROGRESS);
        task2.setUser(user);

        when(taskRepository.findAllByUser(user)).thenReturn(List.of(task1, task2));

        List<Task> result = taskService.getTasks(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
        verify(taskRepository).findAllByUser(user);
    }
}
