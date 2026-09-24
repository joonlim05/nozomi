package com.nozomi.repository;

import com.nozomi.models.TrainStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainStopRepository extends JpaRepository<TrainStop, Long> {
}
