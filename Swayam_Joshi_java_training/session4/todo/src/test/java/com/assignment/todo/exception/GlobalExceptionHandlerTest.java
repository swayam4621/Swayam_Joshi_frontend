package com.assignment.todo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_ShouldReturn404() {
        //Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Task not found");
        
        //Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleNotFound(ex);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found", response.getBody().get("error"));
    }

    @Test
    void handleIllegalArgument_ShouldReturn400() {
        //Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid status");
        
        //Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleIllegalArgument(ex);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid status", response.getBody().get("error"));
    }
}
