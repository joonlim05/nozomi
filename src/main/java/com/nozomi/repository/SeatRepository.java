package com.nozomi.repository;

import com.nozomi.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("""
        SELECT s FROM Seat s
          WHERE s.train.trainName = :trainName
          AND s.carNumber = :carNumber
          AND s.seatNumber = :seatNumber
    """)
    Optional<Seat> findSeatByDetails(
            String trainName,
            Integer carNumber,
            String seatNumber
    );
}
