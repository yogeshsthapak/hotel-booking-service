package io.github.yogeshsthapak.hotelbooking.strategy.payment;

import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.UPI;
    }

    @Override
    public PaymentGatewayResult process(String bookingId, BigDecimal amount, Map<String, String> metadata) {
        String upiId = metadata != null ? metadata.get("upiId") : null;
        if (upiId == null || !upiId.contains("@")) {
            return PaymentGatewayResult.declined("Invalid UPI ID");
        }
        return PaymentGatewayResult.approved("UPI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
