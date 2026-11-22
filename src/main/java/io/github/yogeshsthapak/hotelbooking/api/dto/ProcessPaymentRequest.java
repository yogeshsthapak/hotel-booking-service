package io.github.yogeshsthapak.hotelbooking.api.dto;

import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ProcessPaymentRequest(
        @NotNull(message = "Payment method is required") PaymentMethod method,
        Map<String, String> metadata
) {}
