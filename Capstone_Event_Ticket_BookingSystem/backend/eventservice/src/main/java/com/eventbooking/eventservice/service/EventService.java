package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.dto.EventRequest;
import com.eventbooking.eventservice.entity.Event;
import com.eventbooking.eventservice.exception.EventNotFoundException;
import com.eventbooking.eventservice.exception.UnauthorizedAccessException;
import com.eventbooking.eventservice.exception.PastEventCreationException;
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

    // Create event method
    public Event createEvent(EventRequest request, String organizerEmail) {
        Event event = new Event();

        if (request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new PastEventCreationException("Event date and time cannot be in the past.");
        }
        event.setName(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDateTime(request.getEventDate());
        event.setVenue(request.getLocation());
        event.setTicketPrice(request.getPrice());
        event.setTotalSeats(request.getTotalTickets());
        event.setAvailableSeats(request.getTotalTickets());
        event.setImageUrl(request.getImageUrl());
        event.setArtistName(request.getArtistName());
        event.setCategory(request.getCategory());
        event.setOrganizerEmail(organizerEmail);

        return eventRepository.save(event);
    }

    // Update event service
    public Event updateEvent(Long eventId, EventRequest request, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " could not be found."));

        if (request.getEventDate().isBefore(LocalDateTime.now())) {
            throw new PastEventCreationException("Event date and time cannot be in the past.");
        }

        // prevents a NullPointerException if event.getOrganizerEmail() is null
        if (!organizerEmail.equals(event.getOrganizerEmail())) {
            throw new UnauthorizedAccessException("Not authorized to update this event.");
        }

        event.setName(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setEventDateTime(request.getEventDate());
        event.setVenue(request.getLocation());
        event.setTicketPrice(request.getPrice());
        event.setTotalSeats(request.getTotalTickets());
        event.setImageUrl(request.getImageUrl());
        event.setArtistName(request.getArtistName());

        return eventRepository.save(event);
    }

    // Get events in organizer dash with optional filter
    public List<Event> getEventsByOrganizer(String organizerEmail, String filter) {
        LocalDateTime now = LocalDateTime.now();

        if ("upcoming".equalsIgnoreCase(filter)) {
            return eventRepository.findByOrganizerEmailAndStatusAndEventDateTimeAfterOrderByEventDateTimeAsc(
                    organizerEmail, Event.EventStatus.ACTIVE, now);
        } else if ("past".equalsIgnoreCase(filter)) {
            return eventRepository.findByOrganizerEmailAndStatusAndEventDateTimeBeforeOrderByEventDateTimeDesc(
                    organizerEmail, Event.EventStatus.ACTIVE, now);
        } else if ("cancelled".equalsIgnoreCase(filter)) {
            return eventRepository.findByOrganizerEmailAndStatusOrderByEventDateTimeDesc(
                    organizerEmail, Event.EventStatus.CANCELLED_BY_ORGANIZER);
        }
        return eventRepository.findByOrganizerEmail(organizerEmail);
    }

    // Get all events in customer view
    public List<Event> getAllEvents(String filter) {
        LocalDateTime now = LocalDateTime.now();

        if ("upcoming".equalsIgnoreCase(filter)) {
            return eventRepository.findByStatusAndEventDateTimeAfterOrderByEventDateTimeAsc(
                    Event.EventStatus.ACTIVE, now);
        } else if ("past".equalsIgnoreCase(filter)) {
            return eventRepository.findByStatusAndEventDateTimeBeforeOrderByEventDateTimeDesc(
                    Event.EventStatus.ACTIVE, now);
        } else if ("artists".equalsIgnoreCase(filter)) {
            return eventRepository
                    .findByStatusAndEventDateTimeAfterAndArtistNameIsNotNullAndArtistNameNotOrderByEventDateTimeAsc(
                            Event.EventStatus.ACTIVE, now, "");
        }
        return eventRepository.findAll();
    }

    // Get event by id in both organizer and customer dashbrds
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " could not be found."));
    }

    // Cancel event service
    public Event cancelEvent(Long eventId, String organizerEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " could not be found."));

        if (!organizerEmail.equals(event.getOrganizerEmail())) {
            throw new UnauthorizedAccessException("Not authorized to cancel this event.");
        }

        event.setStatus(Event.EventStatus.CANCELLED_BY_ORGANIZER);
        return eventRepository.save(event);
    }

    // Search and filter events in customer dash
    public List<Event> searchAndFilterActiveEvents(String keyword, String category, String timeframe) {
        List<Event> activeEvents = eventRepository.findByStatus(Event.EventStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();

        return activeEvents.stream()
                .filter(event -> {

                    LocalDateTime eventDate = event.getEventDateTime();
                    if (eventDate == null || eventDate.isBefore(now)) {
                        return false;
                    }
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String searchKey = keyword.toLowerCase().trim();
                        String name = event.getName() != null ? event.getName().toLowerCase() : "";
                        String venue = event.getVenue() != null ? event.getVenue().toLowerCase() : "";

                        if (!name.contains(searchKey) && !venue.contains(searchKey)) {
                            return false;
                        }
                    }

                    if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
                        if (event.getCategory() == null || !event.getCategory().equalsIgnoreCase(category)) {
                            return false;
                        }
                    }
                    if (timeframe != null && !timeframe.trim().isEmpty() && !timeframe.equalsIgnoreCase("All")) {
                        if (eventDate == null) {
                            return false;
                        }

                        if (timeframe.equalsIgnoreCase("Today")) {
                            if (!eventDate.toLocalDate().isEqual(now.toLocalDate()))
                                return false;
                        } else if (timeframe.equalsIgnoreCase("Tomorrow")) {
                            if (!eventDate.toLocalDate().isEqual(now.toLocalDate().plusDays(1)))
                                return false;
                        } else if (timeframe.equalsIgnoreCase("This Weekend")) {
                            java.time.DayOfWeek day = eventDate.getDayOfWeek();
                            boolean isWeekend = (day == java.time.DayOfWeek.SATURDAY
                                    || day == java.time.DayOfWeek.SUNDAY);
                            boolean isUpcoming = eventDate.isAfter(now.minusDays(1))
                                    && eventDate.isBefore(now.plusDays(7));
                            if (!isWeekend || !isUpcoming)
                                return false;
                        }
                    }

                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}