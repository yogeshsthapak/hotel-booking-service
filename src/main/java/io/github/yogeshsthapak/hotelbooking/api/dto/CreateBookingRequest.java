package io.github.yogeshsthapak.hotelbooking.api.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateBookingRequest(
        @NotBlank(message = "Property ID is required") String propertyId,
        @NotBlank(message = "RoomType ID is required") String roomTypeId,
        @NotBlank(message = "Guest name is required") String guestName,
        @NotBlank(message = "Guest email is required") @Email String guestEmail,
        @NotNull(message = "Check-in date is required") LocalDate checkIn,
        @NotNull(message = "Check-out date is required") LocalDate checkOut,
        @Min(value = 1, message = "Guest count must be at least 1") int guestCount
) {}
