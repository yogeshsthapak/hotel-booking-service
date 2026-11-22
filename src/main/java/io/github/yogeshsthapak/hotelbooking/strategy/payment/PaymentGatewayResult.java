package io.github.yogeshsthapak.hotelbooking.strategy.payment;

public record PaymentGatewayResult(
        boolean success,
        String transactionReference,
        String failureReason
) {
    public static PaymentGatewayResult approved(String transactionReference) {
        return new PaymentGatewayResult(true, transactionReference, null);
    }

    public static PaymentGatewayResult declined(String failureReason) {
        return new PaymentGatewayResult(false, null, failureReason);
    }
}
