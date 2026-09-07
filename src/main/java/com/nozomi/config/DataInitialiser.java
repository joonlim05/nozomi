package com.nozomi.config;

import com.nozomi.models.Station;
import com.nozomi.models.Train;
import com.nozomi.models.TrainStop;
import com.nozomi.repository.StationRepository;
import com.nozomi.repository.TrainRepository;
import com.nozomi.repository.TrainStopRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
// this is the class DECLARATION
public class DataInitialiser implements CommandLineRunner {
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final TrainStopRepository trainStopRepository;

    // this is the class CONSTRUCTOR
    public DataInitialiser(
            StationRepository stationRepository,
            TrainRepository trainRepository,
            TrainStopRepository trainStopRepository
    ) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.trainStopRepository = trainStopRepository;
    }

    // this is used to run the actual data seeding procedure. @Override is for safety check (ie. typo)
    @Override
    public void run(String @NonNull ... args) {
        // check if data already exists
        if (stationRepository.count() > 0) {
            return;
        }

        Station tokyo = new Station(null, "Tokyo", "TYO");
        Station shinagawa = new Station(null, "Shinagawa", "SGN");
        Station shinYokohama = new Station(null, "Shin-Yokohama", "SYO");
        Station nagoya = new Station(null, "Nagoya", "NGO");
        Station kyoto = new Station(null, "Kyoto", "KYO");
        Station shinOsaka = new Station(null, "Shin-Osaka", "OSA");

        stationRepository.saveAll(List.of(tokyo, shinagawa, shinYokohama, nagoya, kyoto, shinOsaka));


        Train nozomi1 = new Train(null, "Nozomi 1");
        trainRepository.save(nozomi1);

        // 4. Create and persist the Train Stops (0-indexed sequence)
        // Note: null arrival for origin, null departure for terminus
        List<TrainStop> stops = List.of(
                new TrainStop(null, nozomi1, tokyo, 0, null, LocalTime.of(6, 0)),
                new TrainStop(null, nozomi1, shinagawa, 1, LocalTime.of(6, 6), LocalTime.of(6, 7)),
                new TrainStop(null, nozomi1, shinYokohama, 2, LocalTime.of(6, 18), LocalTime.of(6, 19)),
                new TrainStop(null, nozomi1, nagoya, 3, LocalTime.of(7, 34), LocalTime.of(7, 36)),
                new TrainStop(null, nozomi1, kyoto, 4, LocalTime.of(8, 9), LocalTime.of(8, 11)),
                new TrainStop(null, nozomi1, shinOsaka, 5, LocalTime.of(8, 24), null)
        );

        trainStopRepository.saveAll(stops);

        System.out.println("=== DATA SEEDING COMPLETE ===");
        System.out.println("Stations in DB: " + stationRepository.count());
        System.out.println("Trains in DB:   " + trainRepository.count());
        System.out.println("Stops in DB:    " + trainStopRepository.count());
    }
}
