package io.github.yogeshsthapak.hotelbooking.service;

import io.github.yogeshsthapak.hotelbooking.domain.exception.DomainException;
import io.github.yogeshsthapak.hotelbooking.domain.exception.InventoryUnavailableException;
import io.github.yogeshsthapak.hotelbooking.domain.exception.ResourceNotFoundException;
import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import io.github.yogeshsthapak.hotelbooking.repository.BookingRepository;
import io.github.yogeshsthapak.hotelbooking.strategy.pricing.PricingStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BookingService {
    private final PropertyService propertyService;
    private final InventoryService inventoryService;
    private final BookingRepository bookingRepository;
    private final PricingStrategy pricingStrategy;
    private final Map<String, ReentrantLock> inventoryLocks = new ConcurrentHashMap<>();

    public BookingService(PropertyService propertyService,
                          InventoryService inventoryService,
                          BookingRepository bookingRepository,
                          PricingStrategy pricingStrategy) {
        this.propertyService = propertyService;
        this.inventoryService = inventoryService;
        this.bookingRepository = bookingRepository;
        this.pricingStrategy = pricingStrategy;
    }

    public Booking createBooking(String propertyId, String roomTypeId, String guestName,
                                 String guestEmail, DateRange dateRange, int guestCount) {
        Property property = propertyService.getProperty(propertyId);
        RoomType roomType = property.getRoomTypeOrThrow(roomTypeId);

        if (!roomType.canAccommodate(guestCount)) {
            throw new DomainException("Guest count " + guestCount + " exceeds room capacity of " + roomType.getMaxGuests());
        }

        String lockKey = propertyId + ":" + roomTypeId;
        ReentrantLock lock = inventoryLocks.computeIfAbsent(lockKey, k -> new ReentrantLock());

        lock.lock();
        try {
            int available = inventoryService.getAvailableRooms(propertyId, roomType, dateRange);
            if (available <= 0) {
                throw new InventoryUnavailableException(
                        "No rooms available for room type " + roomType.getName() + " in requested dates");
            }

            BigDecimal totalAmount = pricingStrategy.calculateTotalPrice(roomType, dateRange);
            Booking booking = new Booking(propertyId, roomTypeId, guestName, guestEmail, dateRange, guestCount, totalAmount);
            return bookingRepository.save(booking);
        } finally {
            lock.unlock();
        }
    }

    public Booking getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    }
}
