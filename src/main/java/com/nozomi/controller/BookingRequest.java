package com.nozomi.controller;

public record BookingRequest(
        String trainName,      // e.g. "Nozomi1"
        Integer carNumber,     // e.g. 3
        String seatNumber,     // e.g. "12A"
        String userId          // e.g. "user_123"
) {}