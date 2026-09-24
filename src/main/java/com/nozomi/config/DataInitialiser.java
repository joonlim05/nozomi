package com.nozomi.config;

import com.nozomi.models.Seat;
import com.nozomi.models.Station;
import com.nozomi.models.Train;
import com.nozomi.models.TrainStop;
import com.nozomi.repository.SeatRepository;
import com.nozomi.repository.StationRepository;
import com.nozomi.repository.TrainRepository;
import com.nozomi.repository.TrainStopRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
// this is the class DECLARATION
public class DataInitialiser implements CommandLineRunner {
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final TrainStopRepository trainStopRepository;
    private final SeatRepository seatRepository;

    // this is the class CONSTRUCTOR
    public DataInitialiser(
            StationRepository stationRepository,
            TrainRepository trainRepository,
            TrainStopRepository trainStopRepository, SeatRepository seatRepository
    ) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.trainStopRepository = trainStopRepository;
        this.seatRepository = seatRepository;
    }

    // this is used to run the actual data seeding procedure. @Override is for safety check (ie. typo)
    @Override
    public void run(String @NonNull ... args) {
        // check if data already exists
        if (stationRepository.count() > 0) {
            return;
        }

        Station tokyo = new Station("Tokyo", "TYO");
        Station shinagawa = new Station("Shinagawa", "SGN");
        Station shinYokohama = new Station("Shin-Yokohama", "SYO");
        Station nagoya = new Station("Nagoya", "NGO");
        Station kyoto = new Station("Kyoto", "KYO");
        Station shinOsaka = new Station("Shin-Osaka", "OSA");

        stationRepository.saveAll(List.of(tokyo, shinagawa, shinYokohama, nagoya, kyoto, shinOsaka));


        Train nozomi1 = new Train("Nozomi 1");
        trainRepository.save(nozomi1);

        // null arrival for origin, null departure for terminus
        List<TrainStop> stops = List.of(
                new TrainStop(nozomi1, tokyo, 0, null, LocalTime.of(6, 0)),
                new TrainStop(nozomi1, shinagawa, 1, LocalTime.of(6, 6), LocalTime.of(6, 7)),
                new TrainStop(nozomi1, shinYokohama, 2, LocalTime.of(6, 18), LocalTime.of(6, 19)),
                new TrainStop(nozomi1, nagoya, 3, LocalTime.of(7, 34), LocalTime.of(7, 36)),
                new TrainStop(nozomi1, kyoto, 4, LocalTime.of(8, 9), LocalTime.of(8, 11)),
                new TrainStop(nozomi1, shinOsaka, 5, LocalTime.of(8, 24), null)
        );

        trainStopRepository.saveAll(stops);

        // i'm creating all the seats in memory then sending them one shot to the db
        List<Seat> seats = new ArrayList<>();
        char[] letters = {'A', 'B', 'C', 'D', 'E'};

        for (int car = 1; car <= 16; car++) {
            for (int i = 1; i <= 20; i++) {
                for (char letter : letters) {
                    String seatNumber = Integer.toString(i) + letter;
                    Seat seat = new Seat(seatNumber, car, nozomi1);
                    seats.add(seat);
                }
            }
        }

        seatRepository.saveAll(seats);
    }
}