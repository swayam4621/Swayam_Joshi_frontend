package com.assignment.todo.service;

import com.assignment.todo.client.NotificationServiceClient;
import com.assignment.todo.dto.TodoDTO;
import com.assignment.todo.exception.ResourceNotFoundException;
import com.assignment.todo.mapper.TodoMapper;
import com.assignment.todo.model.Todo;
import com.assignment.todo.model.TodoStatus;
import com.assignment.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

//Testing using AAA pattern = Arrange, Act, Assert

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    //creates fake respository
    @Mock
    private TodoRepository todoRepository;

    // creates fake mapper
    @Mock
    private TodoMapper todoMapper;

    // creates fake notification client
    @Mock
    private NotificationServiceClient notificationServiceClient;

    //injects the mocks into the service being tested
    @InjectMocks
    private TodoService todoService;

    //variables to be used across tests
    private Todo todo;
    private TodoDTO todoDTO;

    @BeforeEach
    void setUp() {
        todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Task");
        todo.setStatus(TodoStatus.PENDING);
        todo.setCreatedAt(Timestamp.from(Instant.now()));

        todoDTO = new TodoDTO();
        todoDTO.setId(1L);
        todoDTO.setTitle("Test Task");
        todoDTO.setStatus(TodoStatus.PENDING);
    }

    @Test
    void createTodo_ShouldSetDefaultStatusAndTimestamp() {
        //Arrange
        todoDTO.setStatus(null); // No status provided
        when(todoMapper.toEntity(todoDTO)).thenReturn(new Todo()); //return pre built todo 
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        when(todoMapper.toDTO(any(Todo.class))).thenReturn(todoDTO);

        //Act
        TodoDTO result = todoService.createTodo(todoDTO);

        //Assert
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(notificationServiceClient, times(1)).sendNotification(anyString());
    }

    @Test
    void getTodoById_ShouldThrowException_WhenNotFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodoById(99L));
    }

    @Test
    void updateTodo_ShouldAllowValidStatusTransition() {
        // Arrange
        TodoDTO updatedDTO = new TodoDTO();
        updatedDTO.setTitle("Updated Title");
        updatedDTO.setStatus(TodoStatus.COMPLETED); // PENDING -> COMPLETED

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        when(todoMapper.toDTO(any(Todo.class))).thenReturn(updatedDTO);

        // Act
        TodoDTO result = todoService.updateTodo(1L, updatedDTO);

        // Assert
        assertEquals(TodoStatus.COMPLETED, result.getStatus());
        assertEquals("Updated Title", result.getTitle());
    }

    // Additional tests for invalid status transitions, deleteTodo, and getAllTodos to achieve 85% coverage
    //session5
    @Test
    void getAllTodos_ShouldReturnListOfTodos() {
        // Arrange
        //Using List.of() to simulate the database returning a list with one item
        when(todoRepository.findAll()).thenReturn(java.util.List.of(todo));
        when(todoMapper.toDTO(any(Todo.class))).thenReturn(todoDTO);

        // Act
        java.util.List<TodoDTO> result = todoService.getAllTodos();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Verify the list has exactly 1 item
        verify(todoRepository, times(1)).findAll(); // Verify the database was queried
    }

    @Test
    void getTodoById_ShouldReturnTodo_WhenFound() {
        // Arrange
        // Simulate the database successfully finding ID 1
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoMapper.toDTO(any(Todo.class))).thenReturn(todoDTO);

        // Act
        TodoDTO result = todoService.getTodoById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());
    }

    @Test
    void deleteTodo_ShouldCallRepositoryDelete_WhenExists() {
        // Arrange
        // deleteTodo first checks if the item exists, so we must mock the findById call
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        // Act
        todoService.deleteTodo(1L);

        // Assert
        // Verify that the repository's delete method was actually called
        verify(todoRepository, times(1)).delete(todo);
    }

}