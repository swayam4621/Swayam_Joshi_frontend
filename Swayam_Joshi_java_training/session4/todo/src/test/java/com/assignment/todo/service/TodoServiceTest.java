package com.assignment.todo.service;

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
        when(todoMapper.toEntity(todoDTO)).thenReturn(new Todo());
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);
        when(todoMapper.toDTO(any(Todo.class))).thenReturn(todoDTO);

        //Act
        TodoDTO result = todoService.createTodo(todoDTO);

        //Assert
        assertNotNull(result);
        verify(todoRepository, times(1)).save(any(Todo.class));
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
}