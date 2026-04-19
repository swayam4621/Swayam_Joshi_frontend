package com.assignment.todo.controller;

import com.assignment.todo.dto.TodoDTO;
import com.assignment.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private TodoDTO todoDTO;

    @BeforeEach
    void setUp() {
        todoDTO = new TodoDTO();
        todoDTO.setId(1L);
        todoDTO.setTitle("Test Task");
        todoDTO.setDescription("Test Description");
    }

    @Test
    void createTodo_ShouldReturn201() throws Exception {
        when(todoService.createTodo(any(TodoDTO.class))).thenReturn(todoDTO);

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void getAllTodos_ShouldReturn200() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of(todoDTO));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getTodoById_ShouldReturn200() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(todoDTO);

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void updateTodo_ShouldReturn200() throws Exception {
        when(todoService.updateTodo(eq(1L), any(TodoDTO.class))).thenReturn(todoDTO);

        mockMvc.perform(put("/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    void deleteTodo_ShouldReturn200AndMessage() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);

        mockMvc.perform(delete("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}