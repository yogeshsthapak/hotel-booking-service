package io.github.yogeshsthapak.hotelbooking.domain.model;

import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Payment {
    private final String id;
    private final String bookingId;
    private final String idempotencyKey;
    private final PaymentMethod method;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String transactionReference;
    private final Instant processedAt;

    public Payment(String bookingId, String idempotencyKey, PaymentMethod method,
                   BigDecimal amount, PaymentStatus status, String transactionReference) {
        this.id = UUID.randomUUID().toString();
        this.bookingId = bookingId;
        this.idempotencyKey = idempotencyKey;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.transactionReference = transactionReference;
        this.processedAt = Instant.now();
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public PaymentMethod getMethod() { return method; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionReference() { return transactionReference; }
    public Instant getProcessedAt() { return processedAt; }
}
