package com.assignment.user_management_system.controller;

import com.assignment.user_management_system.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/message")
    public ResponseEntity<Map<String, String>> getFormattedMessage(@RequestParam String type) {
        // Calling the service to handle the logic
        String result = messageService.formatMessage(type, "Swayam");
        return ResponseEntity.ok(Map.of("formattedMessage", result));
    }
}