package com.assignment.todo.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationServiceClientTest {

    @Test
    void sendNotification_ShouldLogMessageWithoutThrowingException() {
        NotificationServiceClient client = new NotificationServiceClient();
        
        //Just verify it runs without crashing
        assertDoesNotThrow(() -> client.sendNotification("Test Message"));
    }
}