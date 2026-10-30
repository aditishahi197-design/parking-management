package com.example.parking.service;

import com.example.parking.dto.CreateBookingRequest;
import com.example.parking.dto.CreateSlotRequest;
import com.example.parking.exception.ConflictException;
import com.example.parking.exception.NotFoundException;
import com.example.parking.model.ParkingSlot;
import com.example.parking.model.VehicleBooking;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.repository.VehicleBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingService {
    private static final long BILLING_SECONDS = 10_800;
    private static final BigDecimal RATE = new BigDecimal("50.00");

    private final ParkingSlotRepository slotRepository;
    private final VehicleBookingRepository bookingRepository;

    public ParkingService(ParkingSlotRepository slotRepository,
                          VehicleBookingRepository bookingRepository) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public ParkingSlot createSlot(CreateSlotRequest request) {
        String number = request.slotNumber().trim().toUpperCase();
        if (slotRepository.existsBySlotNumberIgnoreCase(number)) {
            throw new ConflictException("Slot number already exists");
        }
        return slotRepository.save(new ParkingSlot(number));
    }

    public List<ParkingSlot> getAvailableSlots() {
        return slotRepository.findByOccupiedFalse();
    }

    @Transactional
    public VehicleBooking bookSlot(CreateBookingRequest request) {
        String vehicleNumber = request.vehicleNumber().trim().toUpperCase();

        if (bookingRepository.existsByVehicleNumberAndExitTimeIsNull(vehicleNumber)) {
            throw new ConflictException("This vehicle already has an active booking");
        }

        ParkingSlot slot = slotRepository.findByIdForUpdate(request.slotId())
                .orElseThrow(() -> new NotFoundException("Parking slot not found: " + request.slotId()));

        if (slot.isOccupied()) {
            throw new ConflictException("The selected parking slot is already occupied");
        }

        slot.setOccupied(true);
        VehicleBooking booking = new VehicleBooking();
        booking.setVehicleNumber(vehicleNumber);
        booking.setSlot(slot);
        booking.setEntryTime(LocalDateTime.now());
        booking.setAmount(BigDecimal.ZERO.setScale(2));
        booking.setPaymentStatus("PENDING");

        slotRepository.save(slot);
        return bookingRepository.save(booking);
    }

    public List<VehicleBooking> getActiveBookings() {
        return bookingRepository.findByExitTimeIsNull();
    }

    public BigDecimal calculateFare(LocalDateTime entryTime, LocalDateTime endTime) {
        if (entryTime == null || endTime == null) {
            throw new IllegalArgumentException("Entry and calculation timestamps are required");
        }
        if (endTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Calculation time cannot be before entry time");
        }

        long seconds = Duration.between(entryTime, endTime).getSeconds();
        if (seconds == 0) {
            return BigDecimal.ZERO.setScale(2);
        }

        long blocks = 1 + (seconds - 1) / BILLING_SECONDS;
        return RATE.multiply(BigDecimal.valueOf(blocks));
    }

    public BigDecimal calculateCurrentFare(Long bookingId, LocalDateTime calculationTime) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        LocalDateTime endTime = calculationTime == null
                ? (booking.getExitTime() == null ? LocalDateTime.now() : booking.getExitTime())
                : calculationTime;

        if (booking.getExitTime() != null && endTime.isAfter(booking.getExitTime())) {
            throw new IllegalArgumentException("Calculation time cannot be after checkout");
        }
        return calculateFare(booking.getEntryTime(), endTime);
    }

    @Transactional
    public VehicleBooking checkout(Long bookingId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (booking.getExitTime() != null) {
            throw new ConflictException("This booking has already been checked out");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        booking.setExitTime(exitTime);
        booking.setAmount(calculateFare(booking.getEntryTime(), exitTime));
        //No real payment gateway is connected.
        booking.setPaymentStatus("PAID");

        ParkingSlot slot = slotRepository.findByIdForUpdate(booking.getSlot().getId())
                .orElseThrow(() -> new NotFoundException("Parking slot not found"));
        slot.setOccupied(false);
        slotRepository.save(slot);
        return bookingRepository.save(booking);
    }

    public BigDecimal getRevenue(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }
        return bookingRepository.calculateRevenue(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }
}
