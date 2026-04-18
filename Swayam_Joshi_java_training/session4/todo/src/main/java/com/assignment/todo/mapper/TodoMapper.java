package com.assignment.todo.mapper;

import com.assignment.todo.dto.TodoDTO;
import com.assignment.todo.model.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    //Converts entity to dto to prevent exposing database models to the client
    public TodoDTO toDTO(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setStatus(todo.getStatus());
        dto.setCreatedAt(todo.getCreatedAt());
        return dto;
    }

    //Converts dto to entity for database operations
    public Todo toEntity(TodoDTO dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(dto.getStatus());
        return todo;
    }
}