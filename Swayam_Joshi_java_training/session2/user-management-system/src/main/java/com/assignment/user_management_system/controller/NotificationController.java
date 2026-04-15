package com.assignment.user_management_system.controller;

import com.assignment.user_management_system.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @GetMapping("/notify")
    public ResponseEntity<Map<String, String>> triggerNotification() {
        String result = notificationService.sendSystemNotification();
        return ResponseEntity.ok(Map.of("message", result));
    }
}