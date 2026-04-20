package com.assignment.todo.service;

import com.assignment.todo.dto.TodoDTO;
import com.assignment.todo.exception.ResourceNotFoundException;
import com.assignment.todo.mapper.TodoMapper;
import com.assignment.todo.model.Todo;
import com.assignment.todo.model.TodoStatus;
import com.assignment.todo.repository.TodoRepository;
import com.assignment.todo.client.NotificationServiceClient;
import org.springframework.stereotype.Service;

// imports required for logging session5
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final NotificationServiceClient notificationServiceClient;

    //Only constructor injection and No autowired fields
    public TodoService(TodoRepository todoRepository, TodoMapper todoMapper, NotificationServiceClient notificationServiceClient) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
        this.notificationServiceClient = notificationServiceClient; // Simulating external service client
    }

    // --- CREATE TODO FLOW ---
    public TodoDTO createTodo(TodoDTO todoDTO) {
        Todo todo = todoMapper.toEntity(todoDTO);
        
        //Auto set created timestamp
        todo.setCreatedAt(Timestamp.from(Instant.now()));
        
        //Default timestamp to pending if not provided
        if (todo.getStatus() == null) {
            todo.setStatus(TodoStatus.PENDING);
        }
        
        Todo savedTodo = todoRepository.save(todo);
        log.info("Created new Todo with ID: {}", savedTodo.getId());

        // Simulate sending a notification to an external service
        notificationServiceClient.sendNotification("New Todo created: " + savedTodo.getTitle());

        return todoMapper.toDTO(savedTodo);
    }

    // --- GET ALL TODOS---
    public List<TodoDTO> getAllTodos() {
        log.info("Fetching all todos from the database");
        return todoRepository.findAll().stream()
                .map(todoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // --- GET TODO BY ID ---
    public TodoDTO getTodoById(Long id) {
        log.info("Fetching todo with ID: {}", id);

        Todo todo = findTodoEntityById(id);
        return todoMapper.toDTO(todo);
    }

    // --- UPDATE TODO ---
    public TodoDTO updateTodo(Long id, TodoDTO updatedTodoDTO) {
        log.info("Updating todo with ID: {}", id);
        Todo existingTodo = findTodoEntityById(id);

        // Validation logic
        if (updatedTodoDTO.getStatus() != null) {
            validateStatusTransition(existingTodo.getStatus(), updatedTodoDTO.getStatus());
            existingTodo.setStatus(updatedTodoDTO.getStatus());
        }

        // Update other fields
        existingTodo.setTitle(updatedTodoDTO.getTitle());
        existingTodo.setDescription(updatedTodoDTO.getDescription());

        Todo savedTodo = todoRepository.save(existingTodo);
        log.info("Updated todo with ID: {}", savedTodo.getId());

        // Simulate sending a notification to an external service
        notificationServiceClient.sendNotification("Todo updated: " + savedTodo.getTitle());
        return todoMapper.toDTO(savedTodo);
    }

    // --- DELETE TODO FLOW ---
    public void deleteTodo(Long id) {
        log.info("Deleting todo with ID: {}", id);
        Todo todo = findTodoEntityById(id);
        todoRepository.delete(todo);
        log.info("Deleted todo with ID: {}", id);
    }

    // --- HELPER METHODS ---
    private Todo findTodoEntityById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
    }

    private void validateStatusTransition(TodoStatus currentStatus, TodoStatus newStatus) {
        if (currentStatus == newStatus) return;
        if (currentStatus == TodoStatus.COMPLETED && newStatus == TodoStatus.PENDING) return;
        if (currentStatus == TodoStatus.PENDING && newStatus == TodoStatus.COMPLETED) return;
        
        log.error("Invalid status transition attempted: {} -> {}", currentStatus, newStatus);
        throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + newStatus);
    }
}
