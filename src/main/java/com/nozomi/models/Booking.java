package com.nozomi.models;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table (name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column
    private LocalDate travelDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_station_id", nullable = false)
    private Station fromStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_station_id", nullable = false)
    private Station toStation;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @Builder
    public Booking(String userId, Seat seat, LocalDate travelDate, Station fromStation, Station toStation){
        this.userId = userId;
        this.seat = seat;
        this.travelDate = travelDate;
        this.fromStation = fromStation;
        this.toStation = toStation;
    }
}