package io.github.yogeshsthapak.hotelbooking.domain.model;

import io.github.yogeshsthapak.hotelbooking.domain.enums.BookingStatus;
import io.github.yogeshsthapak.hotelbooking.domain.exception.InvalidStateTransitionException;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Booking {
    private final String id;
    private final String propertyId;
    private final String roomTypeId;
    private final String guestName;
    private final String guestEmail;
    private final DateRange dateRange;
    private final int guestCount;
    private final BigDecimal totalAmount;
    private final Instant createdAt;

    private BookingStatus status;
    private BigDecimal refundAmount;

    public Booking(String propertyId, String roomTypeId, String guestName, String guestEmail,
                   DateRange dateRange, int guestCount, BigDecimal totalAmount) {
        this.id = UUID.randomUUID().toString();
        this.propertyId = propertyId;
        this.roomTypeId = roomTypeId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.dateRange = dateRange;
        this.guestCount = guestCount;
        this.totalAmount = totalAmount;
        this.createdAt = Instant.now();
        this.status = BookingStatus.PENDING_PAYMENT;
        this.refundAmount = BigDecimal.ZERO;
    }

    public void confirm() {
        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new InvalidStateTransitionException(
                    "Cannot confirm booking in state: " + status);
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public void failPayment() {
        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new InvalidStateTransitionException(
                    "Cannot mark payment failed for booking in state: " + status);
        }
        this.status = BookingStatus.PAYMENT_FAILED;
    }

    public void cancel(BigDecimal calculatedRefund) {
        if (status != BookingStatus.PENDING_PAYMENT && status != BookingStatus.CONFIRMED) {
            throw new InvalidStateTransitionException(
                    "Cannot cancel booking in state: " + status);
        }
        this.refundAmount = (status == BookingStatus.CONFIRMED) ? calculatedRefund : BigDecimal.ZERO;
        this.status = BookingStatus.CANCELLED;
    }

    public String getId() { return id; }
    public String getPropertyId() { return propertyId; }
    public String getRoomTypeId() { return roomTypeId; }
    public String getGuestName() { return guestName; }
    public String getGuestEmail() { return guestEmail; }
    public DateRange getDateRange() { return dateRange; }
    public int getGuestCount() { return guestCount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getRefundAmount() { return refundAmount; }
}
