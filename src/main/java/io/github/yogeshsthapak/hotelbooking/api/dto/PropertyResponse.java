package io.github.yogeshsthapak.hotelbooking.api.dto;

import io.github.yogeshsthapak.hotelbooking.domain.enums.Amenity;
import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record PropertyResponse(
        String id,
        String ownerId,
        String name,
        String city,
        String locality,
        String address,
        int starRating,
        Set<Amenity> amenities,
        List<RoomTypeResponse> roomTypes
) {
    public record RoomTypeResponse(
            String id,
            String name,
            int maxGuests,
            int totalInventory,
            BigDecimal basePricePerNight
    ) {
        public static RoomTypeResponse from(RoomType rt) {
            return new RoomTypeResponse(rt.getId(), rt.getName(), rt.getMaxGuests(),
                    rt.getTotalInventory(), rt.getBasePricePerNight());
        }
    }

    public static PropertyResponse from(Property p) {
        return new PropertyResponse(
                p.getId(),
                p.getOwnerId(),
                p.getName(),
                p.getLocation().city(),
                p.getLocation().locality(),
                p.getLocation().address(),
                p.getStarRating(),
                p.getAmenities(),
                p.getRoomTypes().stream().map(RoomTypeResponse::from).toList()
        );
    }
}
