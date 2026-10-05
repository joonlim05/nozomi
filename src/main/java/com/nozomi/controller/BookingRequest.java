package com.nozomi.controller;

import java.time.LocalDate;

public record BookingRequest(
        String trainName,          // e.g. "Nozomi1"
        String startStationCode,    // e.g. "TYO" - where they board
        String endStationCode,      // e.g. "NGO" - where they get off
        Integer carNumber,         // e.g. 3
        String seatNumber,         // e.g. "12A"
        String userId,             // e.g. "user_123"
        LocalDate travelDate       // e.g. 2026-10-15
) {}