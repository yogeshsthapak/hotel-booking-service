package io.github.yogeshsthapak.hotelbooking.strategy.cancellation;

import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface CancellationPolicy {
    BigDecimal calculateRefund(Booking booking, LocalDate cancellationDate);
}
