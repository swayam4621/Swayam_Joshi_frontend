package com.eventbooking.eventservice.service;

import com.eventbooking.eventservice.dto.BookingRequest;
import com.eventbooking.eventservice.entity.Booking;
import com.eventbooking.eventservice.entity.Event;
import com.eventbooking.eventservice.repository.BookingRepository;
import com.eventbooking.eventservice.repository.EventRepository;
import com.eventbooking.eventservice.exception.EventNotFoundException;
import com.eventbooking.eventservice.exception.InsufficientSeatsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public BookingService(BookingRepository bookingRepository, EventRepository eventRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void processBooking(BookingRequest request, String userEmail) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + request.getEventId() + " could not be found."));

        if (event.getAvailableSeats() < request.getNumberOfTickets()) {
            throw new InsufficientSeatsException("Not enough seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() - request.getNumberOfTickets());
        eventRepository.save(event);

        Booking booking = new Booking();
        booking.setEventId(request.getEventId());
        booking.setUserEmail(userEmail);
        booking.setNumberOfTickets(request.getNumberOfTickets());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setStatus("CONFIRMED");
        booking.setBookingDate(LocalDateTime.now());

        bookingRepository.save(booking);
    }
}