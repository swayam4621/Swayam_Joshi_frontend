package com.eventbooking.eventservice.controller;

import com.eventbooking.eventservice.dto.EventRequest;
import com.eventbooking.eventservice.entity.Event;
import com.eventbooking.eventservice.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//use principal to automatically grab the user's email from the JWT token that Spring Security intercepted
import java.security.Principal;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    //principal.getName() extracts the email from the JWT token and passes it to the service layer for event creation
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest request, Principal principal) {
        Event createdEvent = eventService.createEvent(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody EventRequest request, Principal principal) {
        Event updatedEvent = eventService.updateEvent(id, request, principal.getName());
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<Event>> getMyEvents(Principal principal) {
        List<Event> myEvents = eventService.getEventsByOrganizer(principal.getName());
        return ResponseEntity.ok(myEvents);
}
}