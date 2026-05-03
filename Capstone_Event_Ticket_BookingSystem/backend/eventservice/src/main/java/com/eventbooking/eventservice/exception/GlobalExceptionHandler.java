package com.eventbooking.eventservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEventNotFound(EventNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        //returns 404 Not Found
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); 
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        //returns 403 Forbidden
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); 
    }

    @ExceptionHandler(InsufficientSeatsException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientSeats(InsufficientSeatsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        //returns 400 Bad Request
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingCancellationDeadlineException.class)
    public ResponseEntity<Map<String, String>> handleBookingCancellationDeadline(BookingCancellationDeadlineException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        //returns 400 Bad Request
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }   

    @ExceptionHandler(PastEventCreationException.class)
    public ResponseEntity<Map<String, String>> handlePastEventCreation(PastEventCreationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        //returns 400 Bad Request
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}