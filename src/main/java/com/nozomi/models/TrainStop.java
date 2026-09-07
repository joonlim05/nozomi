package com.nozomi.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "trainStop")
public class TrainStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private Integer stopSequence;

    @Column(nullable = true)
    private LocalTime arrivalTime;

    @Column(nullable = true)
    private LocalTime departureTime;

    // @Builder assists with the initialization process (can ignore id, db will generate)
    @Builder
    public TrainStop(Train train, Station station, Integer stopSequence, LocalTime arrivalTime, LocalTime departureTime) {
        this.train = train;
        this.station = station;
        this.stopSequence = stopSequence;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }
}
