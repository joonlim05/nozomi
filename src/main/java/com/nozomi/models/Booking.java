package com.nozomi.models;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column
    private String userId;

    @Column
    private LocalDateTime created_at;

    @Builder
    public Booking(Seat seat, String userId, LocalDateTime created_at){
        this.seat = seat;
        this.userId = userId;
        this.created_at = created_at;
    }
}