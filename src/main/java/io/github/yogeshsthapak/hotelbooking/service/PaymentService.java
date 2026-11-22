package io.github.yogeshsthapak.hotelbooking.service;

import io.github.yogeshsthapak.hotelbooking.domain.enums.BookingStatus;
import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentStatus;
import io.github.yogeshsthapak.hotelbooking.domain.exception.DomainException;
import io.github.yogeshsthapak.hotelbooking.domain.exception.InvalidStateTransitionException;
import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import io.github.yogeshsthapak.hotelbooking.domain.model.Payment;
import io.github.yogeshsthapak.hotelbooking.repository.BookingRepository;
import io.github.yogeshsthapak.hotelbooking.repository.PaymentRepository;
import io.github.yogeshsthapak.hotelbooking.strategy.payment.PaymentGatewayResult;
import io.github.yogeshsthapak.hotelbooking.strategy.payment.PaymentProcessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final Map<PaymentMethod, PaymentProcessor> processors;

    public PaymentService(BookingService bookingService,
                          BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          List<PaymentProcessor> processorList) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.processors = processorList.stream()
                .collect(Collectors.toMap(PaymentProcessor::supportedMethod, Function.identity()));
    }

    public synchronized Payment processPayment(String bookingId, PaymentMethod method,
                                               String idempotencyKey, Map<String, String> metadata) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Booking booking = bookingService.getBooking(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new InvalidStateTransitionException(
                    "Payment cannot be processed for booking in state: " + booking.getStatus());
        }

        PaymentProcessor processor = processors.get(method);
        if (processor == null) {
            throw new DomainException("Unsupported payment method: " + method);
        }

        PaymentGatewayResult result = processor.process(booking.getId(), booking.getTotalAmount(), metadata);

        if (result.success()) {
            booking.confirm();
            bookingRepository.save(booking);
            Payment payment = new Payment(booking.getId(), idempotencyKey, method,
                    booking.getTotalAmount(), PaymentStatus.SUCCESS, result.transactionReference());
            return paymentRepository.save(payment);
        } else {
            booking.failPayment();
            bookingRepository.save(booking);
            Payment payment = new Payment(booking.getId(), idempotencyKey, method,
                    booking.getTotalAmount(), PaymentStatus.FAILED, result.failureReason());
            return paymentRepository.save(payment);
        }
    }
}
