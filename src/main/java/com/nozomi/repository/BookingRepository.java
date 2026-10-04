package com.nozomi.repository;

import com.nozomi.models.Booking;
import com.nozomi.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsBySeatAndTravelDate(Seat seat, LocalDate travelDate);
}
