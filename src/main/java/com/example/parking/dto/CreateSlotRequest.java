package com.example.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateSlotRequest(
        @NotBlank(message = "Slot number is required")
        @Pattern(regexp = "^[A-Za-z0-9-]{1,20}$",
                message = "Slot number must contain 1-20 letters, numbers or hyphens")
        String slotNumber
) {}
