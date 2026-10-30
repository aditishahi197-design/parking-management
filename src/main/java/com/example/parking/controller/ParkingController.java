package com.example.parking.controller;

import com.example.parking.dto.CreateBookingRequest;
import com.example.parking.dto.CreateSlotRequest;
import com.example.parking.model.ParkingSlot;
import com.example.parking.model.VehicleBooking;
import com.example.parking.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ParkingController {
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/slots")
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingSlot createSlot(@Valid @RequestBody CreateSlotRequest request) {
        return parkingService.createSlot(request);
    }

    @GetMapping("/slots/available")
    public List<ParkingSlot> availableSlots() {
        return parkingService.getAvailableSlots();
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleBooking bookSlot(@Valid @RequestBody CreateBookingRequest request) {
        return parkingService.bookSlot(request);
    }

    @GetMapping("/bookings/active")
    public List<VehicleBooking> activeBookings() {
        return parkingService.getActiveBookings();
    }

    @GetMapping("/bookings/{id}/fare")
    public Map<String, Object> calculateFare(
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime at) {
        BigDecimal amount = parkingService.calculateCurrentFare(id, at);
        LocalDateTime effectiveTime = at == null ? LocalDateTime.now() : at;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("bookingId", id);
        response.put("calculationTime", effectiveTime);
        response.put("amount", amount);
        response.put("currency", "INR");
        return response;
    }

    @PostMapping("/bookings/{id}/checkout")
    public VehicleBooking checkout(@PathVariable Long id) {
        return parkingService.checkout(id);
    }

    @GetMapping("/reports/revenue")
    public Map<String, Object> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("date", date);
        response.put("totalRevenue", parkingService.getRevenue(date));
        response.put("currency", "INR");
        return response;
    }
}
