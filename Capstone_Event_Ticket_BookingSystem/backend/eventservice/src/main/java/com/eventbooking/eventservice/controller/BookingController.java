package com.eventbooking.eventservice.controller;

import com.eventbooking.eventservice.dto.BookingRequest;
import com.eventbooking.eventservice.entity.Booking;
import com.eventbooking.eventservice.entity.Event;
import com.eventbooking.eventservice.repository.BookingRepository;
import com.eventbooking.eventservice.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public BookingController(BookingRepository bookingRepository, EventRepository eventRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/create")
    @Transactional //to ensure if payment fails seats arent lost
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Event> eventOpt = eventRepository.findById(request.getEventId());
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Event not found\"}");
        }

        Event event = eventOpt.get();

        //Check if there are enough seats
        if (event.getAvailableSeats() < request.getNumberOfTickets()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Not enough seats available\"}");
        }

        //Minus seats and save event
        event.setAvailableSeats(event.getAvailableSeats() - request.getNumberOfTickets());
        eventRepository.save(event);

        //Save booking
        Booking booking = new Booking();
        booking.setEventId(request.getEventId());
        booking.setUserEmail(userEmail);
        booking.setNumberOfTickets(request.getNumberOfTickets());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setStatus("CONFIRMED");
        booking.setBookingDate(LocalDateTime.now());

        bookingRepository.save(booking);

        return ResponseEntity.ok().body("{\"message\": \"Booking successful!\"}");
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getBookingsForEvent(@PathVariable Long eventId) {
        try {
            List<Booking> attendees = bookingRepository.findByEventId(eventId);
            return ResponseEntity.ok(attendees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Could not fetch attendees\"}");
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(java.security.Principal principal) {
        try {
            String email = principal.getName();
            List<Booking> myBookings = bookingRepository.findByUserEmail(email);
            return ResponseEntity.ok(myBookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Could not fetch bookings\"}");
        }
    }
}