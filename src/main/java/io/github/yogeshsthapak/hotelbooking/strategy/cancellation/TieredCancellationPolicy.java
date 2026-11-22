package io.github.yogeshsthapak.hotelbooking.strategy.cancellation;

import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@Primary
public class TieredCancellationPolicy implements CancellationPolicy {
    @Override
    public BigDecimal calculateRefund(Booking booking, LocalDate cancellationDate) {
        long daysBeforeCheckIn = ChronoUnit.DAYS.between(cancellationDate, booking.getDateRange().checkIn());
        BigDecimal total = booking.getTotalAmount();

        if (daysBeforeCheckIn >= 2) {
            return total;
        } else if (daysBeforeCheckIn == 1) {
            return total.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
