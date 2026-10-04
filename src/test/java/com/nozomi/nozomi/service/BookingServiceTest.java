package com.nozomi.nozomi.service;

import com.nozomi.controller.BookingRequest;
import com.nozomi.models.Seat;
import com.nozomi.models.Train;
import com.nozomi.repository.BookingRepository;
import com.nozomi.repository.SeatRepository;
import com.nozomi.repository.TrainRepository;
import com.nozomi.repository.TrainStopRepository;
import com.nozomi.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private TrainStopRepository trainStopRepository;

    private final LocalDateTime travelDate = LocalDateTime.of(2026, 10, 15, 8, 30);

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        trainStopRepository.deleteAll();
        trainRepository.deleteAll();

        Train train = Train.builder()
                .trainName("Nozomi1")
                .build();
        trainRepository.save(train);

        Seat seat = Seat.builder()
                .train(train)
                .carNumber(3)
                .seatNumber("12A")
                .build();
        seatRepository.save(seat);
    }

    @Test
    @DisplayName("1. Happy path: successfully book an available seat")
    void bookSeat_success() {
        BookingRequest request = new BookingRequest(
                "Nozomi1", 3, "12A", "user_1", travelDate
        );

        Long bookingId = bookingService.bookSeat(request);

        assertNotNull(bookingId);
        assertEquals(1, bookingRepository.count());
    }

    @Test
    @DisplayName("2. Error handling: throw exception if seat does not exist")
    void bookSeat_seatNotFound() {
        BookingRequest request = new BookingRequest(
                "Nozomi1", 3, "99Z", "user_1", travelDate // "99Z" was not seeded
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.bookSeat(request)
        );

        assertTrue(ex.getMessage().contains("Seat not found"));
        assertEquals(0, bookingRepository.count());
    }

    @Test
    @DisplayName("3. Sequential check: second booking fails when seat is already taken")
    void bookSeat_alreadyBooked_sequential() {
        BookingRequest firstRequest = new BookingRequest(
                "Nozomi1", 3, "12A", "user_1", travelDate
        );
        BookingRequest secondRequest = new BookingRequest(
                "Nozomi1", 3, "12A", "user_2", travelDate
        );

        // First user books successfully
        bookingService.bookSeat(firstRequest);

        // Second user tries right after in the same thread
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> bookingService.bookSeat(secondRequest)
        );

        assertTrue(ex.getMessage().contains("already taken"));
        assertEquals(1, bookingRepository.count());
    }

    @Test
    @DisplayName("4. Concurrency: 10 threads trying to book the exact same seat simultaneously")
    void bookSeat_concurrentRequests_exposesRaceCondition() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // startGate holds back all threads so they can strike simultaneously
        CountDownLatch startGate = new CountDownLatch(1);
        // endGate waits until all 10 threads complete before evaluating assertions
        CountDownLatch endGate = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final String userId = "user_" + i;
            executor.submit(() -> {
                try {
                    startGate.await(); // Wait here until startGate.countDown() is called

                    BookingRequest request = new BookingRequest(
                            "Nozomi1", 3, "12A", userId, travelDate
                    );
                    bookingService.bookSeat(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    endGate.countDown();
                }
            });
        }

        // Release all 10 threads at the exact same instant
        startGate.countDown();
        endGate.await();
        executor.shutdown();

        System.out.println("=== Concurrency Test Result ===");
        System.out.println("Successful bookings : " + successCount.get());
        System.out.println("Failed bookings     : " + failureCount.get());
        System.out.println("Total rows in DB    : " + bookingRepository.count());

        // In a thread-safe system, this assertion PASSES (DB count must be 1).
        // In this naive version, this assertion will FAIL (DB count will be > 1).
        assertEquals(1, bookingRepository.count(),
                "Race condition! More than 1 booking was created for the same seat.");
    }
}
