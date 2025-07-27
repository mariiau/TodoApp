package com.example.todo_service;

import com.example.todo_service.model.Task;
import com.example.todo_service.repository.TaskRepository;
import com.example.todo_service.service.TaskService;
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
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTodo_savesTodoWithUser() {
        Long userId = 1L;
        Task task = new Task();
        task.setTitle("Test");
        task.setDescription("This is task description.");
        task.setDueDate(LocalDate.now().plusDays(5));
        task.setStatus(Task.Status.TODO);
        task.setUserId(userId);

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle(task.getTitle());
        savedTask.setDescription(task.getDescription());
        savedTask.setDueDate(task.getDueDate());
        savedTask.setStatus(task.getStatus());
        savedTask.setUserId(userId);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(task, userId);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTodos_returnsAllTodosForUser() {
        Long userId = 1L;

        Task task1 = new Task();
        task1.setTitle("Test");
        task1.setStatus(Task.Status.TODO);
        task1.setUserId(userId);

        Task task2 = new Task();
        task2.setTitle("Test 2");
        task2.setStatus(Task.Status.IN_PROGRESS);
        task2.setUserId(userId);

        when(taskRepository.findAllByUserId(userId)).thenReturn(List.of(task1, task2));

        List<Task> result = taskService.getTasks(userId);

        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
        verify(taskRepository).findAllByUserId(userId);
    }
}
