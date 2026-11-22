package io.github.yogeshsthapak.hotelbooking.domain.model;

import io.github.yogeshsthapak.hotelbooking.domain.enums.Amenity;
import io.github.yogeshsthapak.hotelbooking.domain.exception.DomainException;
import io.github.yogeshsthapak.hotelbooking.domain.exception.ResourceNotFoundException;
import io.github.yogeshsthapak.hotelbooking.domain.vo.Location;

import java.util.*;

public class Property {
    private final String id;
    private final String ownerId;
    private final String name;
    private final Location location;
    private final int starRating;
    private final Set<Amenity> amenities;
    private final Map<String, RoomType> roomTypes;

    public Property(String id, String ownerId, String name, Location location,
                    int starRating, Set<Amenity> amenities, List<RoomType> roomTypeList) {
        if (starRating < 1 || starRating > 5) {
            throw new DomainException("Star rating must be between 1 and 5");
        }
        if (roomTypeList == null || roomTypeList.isEmpty()) {
            throw new DomainException("Property must have at least one room type");
        }
        this.id = (id == null || id.isBlank()) ? UUID.randomUUID().toString() : id;
        this.ownerId = ownerId;
        this.name = name;
        this.location = location;
        this.starRating = starRating;
        this.amenities = amenities == null ? Set.of() : Set.copyOf(amenities);

        Map<String, RoomType> map = new LinkedHashMap<>();
        for (RoomType rt : roomTypeList) {
            map.put(rt.getId(), rt);
        }
        this.roomTypes = Collections.unmodifiableMap(map);
    }

    public RoomType getRoomTypeOrThrow(String roomTypeId) {
        RoomType roomType = roomTypes.get(roomTypeId);
        if (roomType == null) {
            throw new ResourceNotFoundException("RoomType not found: " + roomTypeId);
        }
        return roomType;
    }

    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public int getStarRating() { return starRating; }
    public Set<Amenity> getAmenities() { return amenities; }
    public Collection<RoomType> getRoomTypes() { return roomTypes.values(); }
}
