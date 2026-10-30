package com.example.parking.repository;

import com.example.parking.model.VehicleBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, Long> {
    boolean existsByVehicleNumberAndExitTimeIsNull(String vehicleNumber);
    List<VehicleBooking> findByExitTimeIsNull();

    @Query("""
        SELECT COALESCE(SUM(b.amount), 0)
        FROM VehicleBooking b
        WHERE b.paymentStatus = 'PAID'
          AND b.exitTime >= :start
          AND b.exitTime < :end
        """)
    BigDecimal calculateRevenue(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);
}
