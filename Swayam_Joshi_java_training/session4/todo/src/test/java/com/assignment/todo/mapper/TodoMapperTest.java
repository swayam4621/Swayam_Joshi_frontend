package com.assignment.todo.mapper;

import com.assignment.todo.dto.TodoDTO;
import com.assignment.todo.model.Todo;
import com.assignment.todo.model.TodoStatus;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TodoMapperTest {

    private final TodoMapper todoMapper = new TodoMapper();

    @Test
    void toDTO_ShouldMapAllFieldsCorrectly() {
        // Arrange
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Mapper Test");
        todo.setDescription("Testing the mapper");
        todo.setStatus(TodoStatus.PENDING);
        todo.setCreatedAt(Timestamp.from(Instant.now()));

        // Act
        TodoDTO dto = todoMapper.toDTO(todo);

        // Assert
        assertEquals(todo.getId(), dto.getId());
        assertEquals(todo.getTitle(), dto.getTitle());
        assertEquals(todo.getDescription(), dto.getDescription());
        assertEquals(todo.getStatus(), dto.getStatus());
        assertEquals(todo.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void toEntity_ShouldMapAllFieldsCorrectly() {
        // Arrange
        TodoDTO dto = new TodoDTO();
        dto.setTitle("Mapper Test");
        dto.setDescription("Testing the mapper");
        dto.setStatus(TodoStatus.COMPLETED);

        // Act
        Todo todo = todoMapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTitle(), todo.getTitle());
        assertEquals(dto.getDescription(), todo.getDescription());
        assertEquals(TodoStatus.COMPLETED, todo.getStatus());
    }

}
