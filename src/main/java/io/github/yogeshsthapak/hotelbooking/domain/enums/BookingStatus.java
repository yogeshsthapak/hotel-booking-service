package io.github.yogeshsthapak.hotelbooking.domain.enums;

public enum BookingStatus {
    PENDING_PAYMENT,
    CONFIRMED,
    PAYMENT_FAILED,
    CANCELLED;

    public boolean holdsInventory() {
        return this == PENDING_PAYMENT || this == CONFIRMED;
    }
}
