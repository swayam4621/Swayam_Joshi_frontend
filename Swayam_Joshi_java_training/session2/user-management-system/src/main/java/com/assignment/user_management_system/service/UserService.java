package com.assignment.user_management_system.service;

import com.assignment.user_management_system.model.User;
import com.assignment.user_management_system.repository.UserRepository;
import com.assignment.user_management_system.component.NotificationComponent;
import com.assignment.user_management_system.formatter.MessageFormatter;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NotificationComponent notificationComponent;
    private final Map<String, MessageFormatter> formatters;

    // Strict Constructor Injection
    public UserService(UserRepository userRepository, 
                       NotificationComponent notificationComponent, 
                       Map<String, MessageFormatter> formatters) {
        this.userRepository = userRepository;
        this.notificationComponent = notificationComponent;
        this.formatters = formatters;
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String addUser(User user) {
        userRepository.save(user);
        return notificationComponent.send(); // Triggers notification on create
    }

    public String getFormattedWelcome(Long id, String type) {
        User user = getUser(id);
        MessageFormatter formatter = formatters.get(type.toUpperCase());
        if (formatter == null) throw new IllegalArgumentException("Invalid format type");
        return formatter.format(user.getName());
    }
}