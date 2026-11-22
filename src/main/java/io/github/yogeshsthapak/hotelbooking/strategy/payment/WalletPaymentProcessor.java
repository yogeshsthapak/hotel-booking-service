package io.github.yogeshsthapak.hotelbooking.strategy.payment;

import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class WalletPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.WALLET;
    }

    @Override
    public PaymentGatewayResult process(String bookingId, BigDecimal amount, Map<String, String> metadata) {
        String walletId = metadata != null ? metadata.get("walletId") : null;
        if (walletId == null || walletId.isBlank()) {
            return PaymentGatewayResult.declined("Wallet ID is required");
        }
        return PaymentGatewayResult.approved("WALLET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
