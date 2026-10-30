package com.example.parking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParkingServiceTest {
    private ParkingService service;
    private LocalDateTime entry;

    @BeforeEach
    void setUp() {
        
        service = new ParkingService(null, null);
        entry = LocalDateTime.of(2026, 10, 25, 10, 0);
    }

    @Test
    void zeroDurationCostsZero() {
        assertEquals(new BigDecimal("0.00"), service.calculateFare(entry, entry));
    }

    @Test
    void twoHoursFifteenMinutesCostsFifty() {
        assertEquals(new BigDecimal("50.00"), service.calculateFare(entry, entry.plusMinutes(135)));
    }

    @Test
    void exactlyThreeHoursCostsFifty() {
        assertEquals(new BigDecimal("50.00"), service.calculateFare(entry, entry.plusHours(3)));
    }

    @Test
    void threeHoursFiveMinutesCostsOneHundred() {
        assertEquals(new BigDecimal("100.00"), service.calculateFare(entry, entry.plusMinutes(185)));
    }

    @Test
    void exactlySixHoursCostsOneHundred() {
        assertEquals(new BigDecimal("100.00"), service.calculateFare(entry, entry.plusHours(6)));
    }

    @Test
    void sixHoursAndOneSecondCostsOneHundredFifty() {
        assertEquals(new BigDecimal("150.00"), service.calculateFare(entry, entry.plusHours(6).plusSeconds(1)));
    }

    @Test
    void timeBeforeEntryIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateFare(entry, entry.minusMinutes(1)));
    }

    @Test
    void missingTimestampIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateFare(entry, null));
    }
}
