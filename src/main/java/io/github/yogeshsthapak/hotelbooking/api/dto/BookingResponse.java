package io.github.yogeshsthapak.hotelbooking.api.dto;

import io.github.yogeshsthapak.hotelbooking.domain.enums.BookingStatus;
import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
        String id,
        String propertyId,
        String roomTypeId,
        String guestName,
        String guestEmail,
        LocalDate checkIn,
        LocalDate checkOut,
        int guestCount,
        BigDecimal totalAmount,
        BookingStatus status,
        BigDecimal refundAmount,
        Instant createdAt
) {
    public static BookingResponse from(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getPropertyId(),
                b.getRoomTypeId(),
                b.getGuestName(),
                b.getGuestEmail(),
                b.getDateRange().checkIn(),
                b.getDateRange().checkOut(),
                b.getGuestCount(),
                b.getTotalAmount(),
                b.getStatus(),
                b.getRefundAmount(),
                b.getCreatedAt()
        );
    }
}
