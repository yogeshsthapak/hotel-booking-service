package io.github.yogeshsthapak.hotelbooking.api.controller;

import io.github.yogeshsthapak.hotelbooking.api.dto.BookingResponse;
import io.github.yogeshsthapak.hotelbooking.api.dto.CreateBookingRequest;
import io.github.yogeshsthapak.hotelbooking.api.dto.ProcessPaymentRequest;
import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import io.github.yogeshsthapak.hotelbooking.domain.model.Payment;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import io.github.yogeshsthapak.hotelbooking.service.BookingService;
import io.github.yogeshsthapak.hotelbooking.service.CancellationService;
import io.github.yogeshsthapak.hotelbooking.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final CancellationService cancellationService;

    public BookingController(BookingService bookingService,
                             PaymentService paymentService,
                             CancellationService cancellationService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.cancellationService = cancellationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        DateRange range = new DateRange(request.checkIn(), request.checkOut());
        Booking booking = bookingService.createBooking(
                request.propertyId(), request.roomTypeId(),
                request.guestName(), request.guestEmail(),
                range, request.guestCount()
        );
        return BookingResponse.from(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable String bookingId) {
        return BookingResponse.from(bookingService.getBooking(bookingId));
    }

    @PostMapping("/{bookingId}/pay")
    public Payment payForBooking(
            @PathVariable String bookingId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody ProcessPaymentRequest request
    ) {
        return paymentService.processPayment(bookingId, request.method(), idempotencyKey, request.metadata());
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingResponse cancelBooking(@PathVariable String bookingId) {
        return BookingResponse.from(cancellationService.cancelBooking(bookingId));
    }
}
