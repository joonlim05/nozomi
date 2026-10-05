package com.nozomi.service;

import com.nozomi.controller.BookingRequest;
import com.nozomi.models.Booking;
import com.nozomi.models.Seat;
import com.nozomi.repository.BookingRepository;
import com.nozomi.repository.SeatRepository;
import com.nozomi.repository.TrainRepository;
import com.nozomi.repository.TrainStopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final TrainStopRepository trainStopRepository;

    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository, TrainStopRepository trainStopRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.trainStopRepository = trainStopRepository;
    }

    @Transactional
    public Long bookSeat(BookingRequest bookingRequest) {
        Seat seat = seatRepository.findSeatByDetails(bookingRequest.trainName(), bookingRequest.carNumber(), bookingRequest.seatNumber())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + bookingRequest.seatNumber()));

        boolean isTaken = bookingRepository.existsBySeatAndTravelDate(seat, LocalDate.from(bookingRequest.travelDate()));

        if (isTaken) {
            throw new IllegalStateException("Seat " + bookingRequest.seatNumber() + " is already taken");
        }

        Booking booking = Booking.builder()
                .seat(seat)
                .userId(bookingRequest.userId())
                .travelDate(LocalDate.from(bookingRequest.travelDate()))
                .build();

        Booking saved = bookingRepository.save(booking);

        // TODO 1: Add booking based on the timeslot and journey legs
        int startSeq = trainStopRepository.findStopSequence(
                bookingRequest.trainName(),
                bookingRequest.startStationCode()
        ).orElseThrow(() -> new IllegalArgumentException("Start station not found on train route"));

        int endSeq = trainStopRepository.findStopSequence(
                bookingRequest.trainName(),
                bookingRequest.endStationCode()
        ).orElseThrow(() -> new IllegalArgumentException("End station not found on train route"));


        // TODO 2: Deal with double booking

        return saved.getId();
    }

}
