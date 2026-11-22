package io.github.yogeshsthapak.hotelbooking.repository.impl;

import io.github.yogeshsthapak.hotelbooking.domain.model.Payment;
import io.github.yogeshsthapak.hotelbooking.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {
    private final Map<String, Payment> store = new ConcurrentHashMap<>();
    private final Map<String, Payment> byIdempotencyKey = new ConcurrentHashMap<>();

    @Override
    public Payment save(Payment payment) {
        store.put(payment.getId(), payment);
        if (payment.getIdempotencyKey() != null && !payment.getIdempotencyKey().isBlank()) {
            byIdempotencyKey.put(payment.getIdempotencyKey(), payment);
        }
        return payment;
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(byIdempotencyKey.get(idempotencyKey));
    }

    @Override
    public Optional<Payment> findByBookingId(String bookingId) {
        return store.values().stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .findFirst();
    }
}
