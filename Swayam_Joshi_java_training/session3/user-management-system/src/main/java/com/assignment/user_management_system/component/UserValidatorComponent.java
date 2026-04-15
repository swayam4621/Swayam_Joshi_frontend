package com.assignment.user_management_system.component;

import com.assignment.user_management_system.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserValidatorComponent {

    //keeping user validation in a separate component as for the single responsibility principle
    public void validateForSubmit(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (user.getAge() == null || user.getAge() <= 0) {
            throw new IllegalArgumentException("age must be a valid positive number");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("role cannot be null or empty");
        }
    }
}