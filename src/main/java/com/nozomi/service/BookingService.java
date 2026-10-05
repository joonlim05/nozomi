package com.nozomi.service;

import com.nozomi.controller.BookingRequest;
import com.nozomi.models.Booking;
import com.nozomi.models.Seat;
import com.nozomi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.nozomi.utilities.BitmaskUtil.createMask;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final TrainStopRepository trainStopRepository;
    private final SeatAvailabilityRepository seatAvailabilityRepository;
    private final StationRepository stationRepository;

    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository, TrainStopRepository trainStopRepository, SeatAvailabilityRepository seatAvailabilityRepository, StationRepository stationRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.trainStopRepository = trainStopRepository;
        this.seatAvailabilityRepository = seatAvailabilityRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Long bookSeat(BookingRequest bookingRequest) {
        Seat seat = seatRepository.findSeatByDetails(bookingRequest.trainName(), bookingRequest.carNumber(), bookingRequest.seatNumber())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + bookingRequest.seatNumber()));

        int startSeq = trainStopRepository.findStopSequence(
                bookingRequest.trainName(),
                bookingRequest.startStationCode()
        ).orElseThrow(() -> new IllegalArgumentException("Start station not found on train route"));

        int endSeq = trainStopRepository.findStopSequence(
                bookingRequest.trainName(),
                bookingRequest.endStationCode()
        ).orElseThrow(() -> new IllegalArgumentException("End station not found on train route"));

        int requestMask = createMask(startSeq, endSeq);

        int bookingResult = seatAvailabilityRepository.tryOccupySeat(seat.getId(), requestMask, bookingRequest.travelDate());

        if (bookingResult == 0) {
            throw new IllegalStateException("Seat is already booked for the requested journey segments.");
        }

        Booking booking = Booking.builder()
                .userId(bookingRequest.userId())
                .seat(seat)
                .travelDate(bookingRequest.travelDate())
                .startStation(stationRepository.findByCode(bookingRequest.startStationCode())
                        .orElseThrow(() -> new IllegalArgumentException("Start station not found: " + bookingRequest.startStationCode())))
                .endStation(stationRepository.findByCode(bookingRequest.endStationCode())
                        .orElseThrow(() -> new IllegalArgumentException("End station not found: " + bookingRequest.endStationCode())))
                .build();

        Booking saved = bookingRepository.save(booking);
        return saved.getId();

    }
}
