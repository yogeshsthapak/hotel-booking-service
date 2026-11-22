package io.github.yogeshsthapak.hotelbooking.api.dto;

import io.github.yogeshsthapak.hotelbooking.domain.enums.Amenity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record CreatePropertyRequest(
        @NotBlank(message = "Owner ID is required") String ownerId,
        @NotBlank(message = "Property name is required") String name,
        @NotBlank(message = "City is required") String city,
        @NotBlank(message = "Locality is required") String locality,
        String address,
        @Min(value = 1, message = "Star rating must be at least 1")
        @Max(value = 5, message = "Star rating must be at most 5") int starRating,
        Set<Amenity> amenities,
        @NotEmpty(message = "At least one room type is required")
        @Valid List<RoomTypeRequest> roomTypes
) {
    public record RoomTypeRequest(
            @NotBlank(message = "Room type name is required") String name,
            @Min(value = 1, message = "Max guests must be at least 1") int maxGuests,
            @Min(value = 1, message = "Total inventory must be at least 1") int totalInventory,
            @NotNull(message = "Base price is required")
            @DecimalMin(value = "0.01", message = "Base price must be positive") BigDecimal basePricePerNight
    ) {}
}
