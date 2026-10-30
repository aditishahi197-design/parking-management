package com.example.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateBookingRequest(
        @NotBlank(message = "Vehicle number is required")
        @Pattern(regexp = "^[A-Za-z0-9 -]{1,20}$",
                message = "Vehicle number must contain only letters, numbers, spaces or hyphens")
        String vehicleNumber,

        @NotNull(message = "Slot ID is required")
        @Positive(message = "Slot ID must be positive")
        Long slotId
) {}
