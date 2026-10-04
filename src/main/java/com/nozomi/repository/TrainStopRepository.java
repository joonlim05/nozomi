package com.nozomi.repository;

import com.nozomi.models.TrainStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainStopRepository extends JpaRepository<TrainStop, Long> {
    @Query("""
        SELECT ts.stopSequence
        FROM TrainStop ts
        WHERE ts.train.trainName = :trainName
          AND ts.station.code = :stationCode
    """)
    Optional<Integer> findStopSequence(
           String trainName,
           String stationCode
    );
}
