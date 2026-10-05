package com.nozomi.repository;

import com.nozomi.models.Seat;
import com.nozomi.models.SeatAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface SeatAvailabilityRepository extends JpaRepository<SeatAvailability, Long> {
    @Modifying
    @Query(value = """
                UPDATE seat_availability
                SET bitmap = (bitmap | :requestMask)
                WHERE seat_id = :seat_id
                AND travelDate = :travelDate
                AND (bitmap & :requestMask) = 0
            """, nativeQuery = true)
    int tryOccupySeat(
            Long seat_id,
            int requestMask,
            LocalDate travelDate
    );
}
