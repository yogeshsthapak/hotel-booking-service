package io.github.yogeshsthapak.hotelbooking.strategy.payment;

import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.CARD;
    }

    @Override
    public PaymentGatewayResult process(String bookingId, BigDecimal amount, Map<String, String> metadata) {
        String cardNumber = metadata != null ? metadata.get("cardNumber") : null;
        if (cardNumber == null || cardNumber.length() < 4) {
            return PaymentGatewayResult.declined("Invalid card details provided");
        }
        if (cardNumber.endsWith("0000")) {
            return PaymentGatewayResult.declined("Card declined by issuing bank");
        }
        return PaymentGatewayResult.approved("CARD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
