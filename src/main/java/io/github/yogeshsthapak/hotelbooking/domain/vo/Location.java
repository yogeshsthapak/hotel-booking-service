package io.github.yogeshsthapak.hotelbooking.domain.vo;

import io.github.yogeshsthapak.hotelbooking.domain.exception.DomainException;

public record Location(String city, String locality, String address) {
    public Location {
        if (city == null || city.isBlank()) {
            throw new DomainException("City is required");
        }
        if (locality == null || locality.isBlank()) {
            throw new DomainException("Locality is required");
        }
    }
}
