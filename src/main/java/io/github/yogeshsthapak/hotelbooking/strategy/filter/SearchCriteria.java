package io.github.yogeshsthapak.hotelbooking.strategy.filter;

import io.github.yogeshsthapak.hotelbooking.domain.enums.Amenity;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;

import java.math.BigDecimal;
import java.util.Set;

public record SearchCriteria(
        String city,
        String locality,
        DateRange dateRange,
        Integer guests,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Set<Amenity> requiredAmenities,
        Integer minStarRating
) {
}
