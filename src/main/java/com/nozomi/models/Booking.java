package com.nozomi.models;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @Builder
    public Booking(Seat seat, String userId){
        this.seat = seat;
        this.userId = userId;
    }
}