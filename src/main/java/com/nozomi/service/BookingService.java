package com.nozomi.service;

import com.nozomi.controller.BookingRequest;
import com.nozomi.models.Booking;
import com.nozomi.models.Seat;
import com.nozomi.repository.BookingRepository;
import com.nozomi.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Long bookSeat(BookingRequest bookingRequest){
        Seat seat = seatRepository.findSeatByDetails(bookingRequest.trainName(), bookingRequest.carNumber(), bookingRequest.seatNumber())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + bookingRequest.seatNumber()));

        boolean isTaken = bookingRepository.existsBySeatAndTravelDate(seat, bookingRequest.travelDate());

        if (isTaken){
            throw new IllegalStateException("Seat " + bookingRequest.seatNumber() + " is already taken");
        }

        Booking booking = Booking.builder()
                .seat(seat)
                .userId(bookingRequest.userId())
                .travelDate(bookingRequest.travelDate())
                .build();

        Booking saved = bookingRepository.save(booking);
        return saved.getId();

        // TODO 1: Add booking based on the timeslot and journey legs
        // TODO 2: Deal with double booking
    }

}
