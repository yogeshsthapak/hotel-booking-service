package io.github.yogeshsthapak.hotelbooking.repository;

import io.github.yogeshsthapak.hotelbooking.domain.model.Payment;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    Optional<Payment> findByBookingId(String bookingId);
}
