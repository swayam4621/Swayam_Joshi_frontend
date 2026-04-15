package com.assignment.user_management_system.component;

import org.springframework.stereotype.Component;

@Component
public class NotificationComponent {
    public String send() {
        return "Notification sent successfully";
    }
}