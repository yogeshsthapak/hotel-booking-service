package io.github.yogeshsthapak.hotelbooking.domain.vo;

import io.github.yogeshsthapak.hotelbooking.domain.exception.DomainException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DateRange(LocalDate checkIn, LocalDate checkOut) {
    public DateRange {
        if (checkIn == null || checkOut == null) {
            throw new DomainException("Check-in and check-out dates are required");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new DomainException("Check-in date must be strictly before check-out date");
        }
    }

    public long nights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public boolean overlaps(DateRange other) {
        return this.checkIn.isBefore(other.checkOut) && other.checkIn.isBefore(this.checkOut);
    }
}
