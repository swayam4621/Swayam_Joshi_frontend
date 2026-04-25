package com.eventbooking.eventservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    
    @Column(nullable = false)
    private String customerEmail; 

    @Column(nullable = false)
    private Integer numberOfTickets;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();

    public enum BookingStatus {
        CONFIRMED,
        CANCELLED
    }
}
