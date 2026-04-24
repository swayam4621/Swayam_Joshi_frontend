package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.dto.EventRequest;
import com.eventbooking.eventservice.entity.Event;
import com.eventbooking.eventservice.exception.EventNotFoundException;
import com.eventbooking.eventservice.exception.UnauthorizedAccessException;
import com.eventbooking.eventservice.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(EventRequest request, String organizerEmail) {
        Event event = new Event();
        
        event.setName(request.getTitle()); 
        event.setDescription(request.getDescription());
        event.setEventDateTime(request.getEventDate()); 
        event.setVenue(request.getLocation()); 
        event.setTicketPrice(request.getPrice()); 
        event.setTotalSeats(request.getTotalTickets()); 
        event.setAvailableSeats(request.getTotalTickets()); 
        
        event.setOrganizerEmail(organizerEmail); 

        return eventRepository.save(event);
    }

    public Event updateEvent(Long eventId, EventRequest request, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " could not be found."));

        //prevents a NullPointerException if event.getOrganizerEmail() is  null
        if (!organizerEmail.equals(event.getOrganizerEmail())) {
            throw new UnauthorizedAccessException("Not authorized to update this event.");
        }

        event.setName(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDateTime(request.getEventDate());
        event.setVenue(request.getLocation());
        event.setTicketPrice(request.getPrice());
        event.setTotalSeats(request.getTotalTickets());

        return eventRepository.save(event);
    }

    public List<Event> getEventsByOrganizer(String organizerEmail) {
        return eventRepository.findByOrganizerEmail(organizerEmail);
    }

    public Event getEventById(Long eventId, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " could not be found."));

        //Ensuring the organizer requesting details created the event
        if (!organizerEmail.equals(event.getOrganizerEmail())) {
            throw new UnauthorizedAccessException("Not authorized to view this event.");
        }
        return event;
    }

    public Event cancelEvent(Long eventId, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " could not be found."));

        if (!organizerEmail.equals(event.getOrganizerEmail())) {
            throw new UnauthorizedAccessException("Not authorized to cancel this event.");
        }

        event.setStatus(Event.EventStatus.CANCELLED_BY_ORGANIZER);
        return eventRepository.save(event);
    }
}