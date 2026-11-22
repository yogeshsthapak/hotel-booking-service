package io.github.yogeshsthapak.hotelbooking;

import io.github.yogeshsthapak.hotelbooking.domain.enums.BookingStatus;
import io.github.yogeshsthapak.hotelbooking.domain.exception.DomainException;
import io.github.yogeshsthapak.hotelbooking.domain.exception.InvalidStateTransitionException;
import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import io.github.yogeshsthapak.hotelbooking.strategy.cancellation.TieredCancellationPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DomainRulesTest {

    @Test
    void dateRange_overlapAndAdjacentDates_handledAccurately() {
        DateRange oct10to15 = new DateRange(LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 15));
        DateRange oct15to18 = new DateRange(LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 18));
        DateRange oct12to14 = new DateRange(LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 14));

        // Same-day checkout and check-in should NOT overlap
        assertFalse(oct10to15.overlaps(oct15to18));
        // Nested dates SHOULD overlap
        assertTrue(oct10to15.overlaps(oct12to14));
        assertEquals(5, oct10to15.nights());
    }

    @Test
    void dateRange_checkOutBeforeOrEqualCheckIn_throwsException() {
        LocalDate date = LocalDate.of(2026, 10, 10);
        assertThrows(DomainException.class, () -> new DateRange(date, date));
        assertThrows(DomainException.class, () -> new DateRange(date.plusDays(1), date));
    }

    @Test
    void booking_invalidStateTransitions_throwException() {
        DateRange range = new DateRange(LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
        Booking booking = new Booking("prop-1", "room-1", "Alice", "alice@test.com", range, 2, new BigDecimal("5000"));

        booking.confirm();
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        // Cannot confirm twice
        assertThrows(InvalidStateTransitionException.class, booking::confirm);

        // Cancel confirmed booking
        booking.cancel(new BigDecimal("5000"));
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());

        // Cannot cancel twice
        assertThrows(InvalidStateTransitionException.class, () -> booking.cancel(BigDecimal.ZERO));
    }

    @Test
    void tieredCancellationPolicy_calculatesRefundBasedOnNoticeWindow() {
        TieredCancellationPolicy policy = new TieredCancellationPolicy();
        LocalDate checkIn = LocalDate.of(2026, 12, 10);
        DateRange range = new DateRange(checkIn, checkIn.plusDays(2));
        Booking booking = new Booking("prop-1", "room-1", "Bob", "bob@test.com", range, 2, new BigDecimal("1000.00"));

        // 3 days before -> 100% refund
        assertEquals(new BigDecimal("1000.00"), policy.calculateRefund(booking, checkIn.minusDays(3)));
        // 1 day before -> 50% refund
        assertEquals(new BigDecimal("500.00"), policy.calculateRefund(booking, checkIn.minusDays(1)));
        // Same day -> 0% refund
        assertEquals(BigDecimal.ZERO, policy.calculateRefund(booking, checkIn));
    }
}
