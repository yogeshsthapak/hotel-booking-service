package io.github.yogeshsthapak.hotelbooking;

import io.github.yogeshsthapak.hotelbooking.domain.enums.Amenity;
import io.github.yogeshsthapak.hotelbooking.domain.enums.BookingStatus;
import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentMethod;
import io.github.yogeshsthapak.hotelbooking.domain.enums.PaymentStatus;
import io.github.yogeshsthapak.hotelbooking.domain.exception.InventoryUnavailableException;
import io.github.yogeshsthapak.hotelbooking.domain.model.*;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import io.github.yogeshsthapak.hotelbooking.domain.vo.Location;
import io.github.yogeshsthapak.hotelbooking.service.*;
import io.github.yogeshsthapak.hotelbooking.strategy.filter.SearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotelBookingWorkflowTest {

    @Autowired private PropertyService propertyService;
    @Autowired private DiscoveryService discoveryService;
    @Autowired private BookingService bookingService;
    @Autowired private PaymentService paymentService;
    @Autowired private CancellationService cancellationService;

    @Test
    void fullLifecycle_discoveryBookPayCancelAndRebook() {
        Owner chainOwner = propertyService.registerOwner("Taj Group", "ops@taj.com");

        RoomType deluxe = new RoomType(null, "Deluxe", 2, 1, new BigDecimal("4000"));
        Property property = propertyService.addProperty(
                chainOwner.getId(),
                "Taj MG Road",
                new Location("Bengaluru", "MG Road", "41/3 MG Road"),
                5,
                Set.of(Amenity.WIFI, Amenity.POOL, Amenity.BREAKFAST),
                List.of(deluxe)
        );

        DateRange stayDates = new DateRange(LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
        SearchCriteria criteria = new SearchCriteria(
                "Bengaluru", null, stayDates, 2,
                new BigDecimal("3000"), new BigDecimal("5000"),
                Set.of(Amenity.WIFI, Amenity.POOL), 5
        );

        // 1. Property appears in search before booking
        assertTrue(discoveryService.search(criteria).stream().anyMatch(p -> p.getId().equals(property.getId())));

        // 2. Create booking -> inventory of 1 is now held
        Booking booking = bookingService.createBooking(
                property.getId(), deluxe.getId(), "Yogesh", "yogesh@test.com", stayDates, 2
        );
        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
        assertEquals(new BigDecimal("8000"), booking.getTotalAmount());

        // 3. Property no longer shows as available for those dates
        assertFalse(discoveryService.search(criteria).stream().anyMatch(p -> p.getId().equals(property.getId())));

        // 4. Double-booking same dates fails
        assertThrows(InventoryUnavailableException.class, () ->
                bookingService.createBooking(property.getId(), deluxe.getId(), "Other", "other@test.com", stayDates, 2)
        );

        // 5. Idempotent payment confirms booking
        Payment p1 = paymentService.processPayment(booking.getId(), PaymentMethod.UPI, "idem-key-1", Map.of("upiId", "yogesh@upi"));
        Payment p2 = paymentService.processPayment(booking.getId(), PaymentMethod.UPI, "idem-key-1", Map.of("upiId", "yogesh@upi"));
        assertEquals(PaymentStatus.SUCCESS, p1.getStatus());
        assertEquals(p1.getId(), p2.getId());
        assertEquals(BookingStatus.CONFIRMED, bookingService.getBooking(booking.getId()).getStatus());

        // 6. Cancellation releases inventory and calculates 100% refund (>2 days ahead)
        Booking cancelled = cancellationService.cancelBooking(booking.getId());
        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
        assertEquals(new BigDecimal("8000"), cancelled.getRefundAmount());

        // 7. Property is discoverable again!
        assertTrue(discoveryService.search(criteria).stream().anyMatch(p -> p.getId().equals(property.getId())));
    }

    @Test
    void concurrentBookings_onLimitedInventory_neverOverbooks() throws InterruptedException {
        Owner owner = propertyService.registerOwner("Boutique Host", "host@stay.com");
        RoomType suite = new RoomType(null, "Royal Suite", 4, 2, new BigDecimal("10000"));
        Property property = propertyService.addProperty(
                owner.getId(), "Coorg Retreat",
                new Location("Coorg", "Madikeri", "Hilltop Rd"),
                4, Set.of(Amenity.WIFI), List.of(suite)
        );

        DateRange dates = new DateRange(LocalDate.now().plusDays(20), LocalDate.now().plusDays(22));
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    latch.await();
                    bookingService.createBooking(property.getId(), suite.getId(), "Guest-" + idx, "g" + idx + "@test.com", dates, 2);
                    successCount.incrementAndGet();
                } catch (InventoryUnavailableException e) {
                    conflictCount.incrementAndGet();
                } catch (InterruptedException ignored) {
                }
            });
        }

        latch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(2, successCount.get(), "Exactly 2 rooms should be booked");
        assertEquals(8, conflictCount.get(), "Remaining 8 concurrent requests must be rejected");
    }
}
