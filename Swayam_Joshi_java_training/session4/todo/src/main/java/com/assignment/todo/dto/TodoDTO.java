package com.assignment.todo.dto;

import com.assignment.todo.model.TodoStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;

public class TodoDTO {

    private Long id;
    private Timestamp createdAt;

    //Validations
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, message = "Title must be at least 3 characters long")

    private String title;

    private String description; 

    private TodoStatus status;

    //Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TodoStatus getStatus() { return status; }
    public void setStatus(TodoStatus status) { this.status = status; }
}