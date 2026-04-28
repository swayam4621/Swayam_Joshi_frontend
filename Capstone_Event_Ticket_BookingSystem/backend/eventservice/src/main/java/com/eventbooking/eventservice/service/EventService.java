package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.dto.EventRequest;
import com.eventbooking.eventservice.entity.Event;
import com.eventbooking.eventservice.exception.EventNotFoundException;
import com.eventbooking.eventservice.exception.UnauthorizedAccessException;
import com.eventbooking.eventservice.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

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
        event.setImageUrl(request.getImageUrl());
        event.setArtistName(request.getArtistName());
        
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
        event.setImageUrl(request.getImageUrl());
        event.setArtistName(request.getArtistName());

        return eventRepository.save(event);
    }

    public List<Event> getEventsByOrganizer(String organizerEmail, String filter) {
        LocalDateTime now = LocalDateTime.now();

        if ("upcoming".equalsIgnoreCase(filter)) {
            return eventRepository.findByOrganizerEmailAndStatusAndEventDateTimeAfterOrderByEventDateTimeAsc(
                    organizerEmail, Event.EventStatus.ACTIVE, now);
        } 
        else if ("past".equalsIgnoreCase(filter)) {
            return eventRepository.findByOrganizerEmailAndStatusAndEventDateTimeBeforeOrderByEventDateTimeDesc(
                    organizerEmail, Event.EventStatus.ACTIVE, now);
        } 
        else if ("cancelled".equalsIgnoreCase(filter)) {
            return eventRepository.findByOrganizerEmailAndStatusOrderByEventDateTimeDesc(
                    organizerEmail, Event.EventStatus.CANCELLED_BY_ORGANIZER);
        }
        return eventRepository.findByOrganizerEmail(organizerEmail);
    }

    public List<Event> getAllEvents(String filter) {
        LocalDateTime now = LocalDateTime.now();

        if ("upcoming".equalsIgnoreCase(filter)) {
            return eventRepository.findByStatusAndEventDateTimeAfterOrderByEventDateTimeAsc(
                    Event.EventStatus.ACTIVE, now);
        } 
        else if ("past".equalsIgnoreCase(filter)) {
            return eventRepository.findByStatusAndEventDateTimeBeforeOrderByEventDateTimeDesc(
                    Event.EventStatus.ACTIVE, now);
        }
        else if ("artists".equalsIgnoreCase(filter)) {
            return eventRepository.findByStatusAndEventDateTimeAfterAndArtistNameIsNotNullAndArtistNameNotOrderByEventDateTimeAsc(
                    Event.EventStatus.ACTIVE, now, "");
        }
        //fallback
        return eventRepository.findAll();
    }

    public Event getEventById(Long eventId) {
            return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " could not be found."));
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