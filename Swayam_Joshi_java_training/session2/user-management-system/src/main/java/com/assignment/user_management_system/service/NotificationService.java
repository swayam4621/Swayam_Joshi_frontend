package com.assignment.user_management_system.service;

import com.assignment.user_management_system.component.NotificationComponent;
import com.assignment.user_management_system.formatter.MessageFormatter;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationComponent notificationComponent;

    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    // Task: Service calls a NotificationComponent
    public String sendSystemNotification() {
        return notificationComponent.send();
    }

    // Task: Dynamic Message Formatter logic (No if-else)
    public String getFormattedMessage(String type, String content) {
        MessageFormatter formatter = formatters.get(type.toUpperCase());
        
        if (formatter == null) {
            throw new IllegalArgumentException("Invalid format type: " + type);
        }
        
        return formatter.format(content);
    }
}