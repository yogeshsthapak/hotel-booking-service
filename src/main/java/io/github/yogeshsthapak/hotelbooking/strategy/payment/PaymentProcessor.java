package io.github.yogeshsthapak.hotelbooking.strategy.payment;

import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.Map;

public interface PaymentProcessor {
    PaymentMethod supportedMethod();
    PaymentGatewayResult process(String bookingId, BigDecimal amount, Map<String, String> metadata);
}
