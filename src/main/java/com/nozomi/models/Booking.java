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
    @JoinColumn(name = "start_station_id", nullable = false)
    private Station startStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_station_id", nullable = false)
    private Station endStation;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @Builder
    public Booking(String userId, Seat seat, LocalDate travelDate, Station startStation, Station endStation){
        this.userId = userId;
        this.seat = seat;
        this.travelDate = travelDate;
        this.startStation = startStation;
        this.endStation = endStation;
    }
}