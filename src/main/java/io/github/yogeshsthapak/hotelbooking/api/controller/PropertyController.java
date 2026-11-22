package io.github.yogeshsthapak.hotelbooking.api.controller;

import io.github.yogeshsthapak.hotelbooking.api.dto.CreatePropertyRequest;
import io.github.yogeshsthapak.hotelbooking.api.dto.PropertyResponse;
import io.github.yogeshsthapak.hotelbooking.domain.enums.Amenity;
import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import io.github.yogeshsthapak.hotelbooking.domain.vo.Location;
import io.github.yogeshsthapak.hotelbooking.service.DiscoveryService;
import io.github.yogeshsthapak.hotelbooking.service.PropertyService;
import io.github.yogeshsthapak.hotelbooking.strategy.filter.SearchCriteria;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {
    private final PropertyService propertyService;
    private final DiscoveryService discoveryService;

    public PropertyController(PropertyService propertyService, DiscoveryService discoveryService) {
        this.propertyService = propertyService;
        this.discoveryService = discoveryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse addProperty(@Valid @RequestBody CreatePropertyRequest request) {
        Location location = new Location(request.city(), request.locality(), request.address());
        List<RoomType> roomTypes = request.roomTypes().stream()
                .map(rt -> new RoomType(null, rt.name(), rt.maxGuests(), rt.totalInventory(), rt.basePricePerNight()))
                .toList();

        Property created = propertyService.addProperty(
                request.ownerId(), request.name(), location,
                request.starRating(), request.amenities(), roomTypes
        );
        return PropertyResponse.from(created);
    }

    @GetMapping("/{propertyId}")
    public PropertyResponse getProperty(@PathVariable String propertyId) {
        return PropertyResponse.from(propertyService.getProperty(propertyId));
    }

    @GetMapping("/search")
    public List<PropertyResponse> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Set<Amenity> amenities,
            @RequestParam(required = false) Integer minStarRating
    ) {
        DateRange dateRange = (checkIn != null || checkOut != null) ? new DateRange(checkIn, checkOut) : null;
        SearchCriteria criteria = new SearchCriteria(
                city, locality, dateRange, guests, minPrice, maxPrice, amenities, minStarRating
        );
        return discoveryService.search(criteria).stream()
                .map(PropertyResponse::from)
                .toList();
    }
}
