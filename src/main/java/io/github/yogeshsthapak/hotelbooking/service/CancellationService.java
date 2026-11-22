package io.github.yogeshsthapak.hotelbooking.service;

import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import io.github.yogeshsthapak.hotelbooking.repository.BookingRepository;
import io.github.yogeshsthapak.hotelbooking.strategy.cancellation.CancellationPolicy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CancellationService {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CancellationPolicy cancellationPolicy;

    public CancellationService(BookingService bookingService,
                               BookingRepository bookingRepository,
                               CancellationPolicy cancellationPolicy) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.cancellationPolicy = cancellationPolicy;
    }

    public synchronized Booking cancelBooking(String bookingId) {
        Booking booking = bookingService.getBooking(bookingId);
        BigDecimal refund = cancellationPolicy.calculateRefund(booking, LocalDate.now());
        booking.cancel(refund);
        return bookingRepository.save(booking);
    }
}
