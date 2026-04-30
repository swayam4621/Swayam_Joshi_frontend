package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.dto.EventRequest;
import com.eventbooking.eventservice.entity.Event;
import com.eventbooking.eventservice.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void testCreateEvent_Success() {
        EventRequest request = new EventRequest();
        request.setTitle("Future Concert");
        request.setEventDate(LocalDateTime.now().plusDays(5)); 

        Event savedEvent = new Event();
        savedEvent.setId(1L);
        savedEvent.setName("Future Concert");

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.createEvent(request, "organizer@gmail.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepository, times(1)).save(any(Event.class)); 
    }

    @Test
    void testCreateEvent_FailsWhenDateInPast() {
        EventRequest request = new EventRequest();
        request.setEventDate(LocalDateTime.now().minusDays(1)); 

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.createEvent(request, "organizer@gmail.com");
        });

        assertEquals("Event date and time cannot be in the past.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class)); 
    }
}
