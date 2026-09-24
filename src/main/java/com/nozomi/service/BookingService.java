package com.nozomi.service;

import com.nozomi.controller.BookingRequest;
import com.nozomi.models.Booking;
import com.nozomi.models.Seat;
import com.nozomi.repository.BookingRepository;
import com.nozomi.repository.SeatRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    public void bookSeat(BookingRequest bookingRequest){
        Seat seat = seatRepository.findSeatByDetails(bookingRequest.trainName(), bookingRequest.carNumber(), bookingRequest.seatNumber())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + bookingRequest.seatNumber()));

        boolean isTaken = bookingRepository.existsBySeat(seat);

        if (isTaken){
            throw new IllegalStateException("Seat " + bookingRequest.seatNumber() + " is already taken");
        }

        Booking booking = Booking.builder()
                .seat(seat)
                .userId(bookingRequest.userId()).
                build();

        bookingRepository.save(booking);

        // TODO 1: Add booking based on the timeslot and journey legs
        // TODO 2: Deal with double booking
        // TODO 3: @Transactional. Do smth abt dis
        // TODO 4: Choose a return value to replace void.
    }

}
