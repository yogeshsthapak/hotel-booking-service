package io.github.yogeshsthapak.hotelbooking.service;

import io.github.yogeshsthapak.hotelbooking.domain.model.Booking;
import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;
import io.github.yogeshsthapak.hotelbooking.domain.vo.DateRange;
import io.github.yogeshsthapak.hotelbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final BookingRepository bookingRepository;

    public InventoryService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public int getAvailableRooms(String propertyId, RoomType roomType, DateRange requestedRange) {
        List<Booking> existingBookings = bookingRepository.findByPropertyAndRoomType(propertyId, roomType.getId());

        long overlappingActiveBookings = existingBookings.stream()
                .filter(b -> b.getStatus().holdsInventory())
                .filter(b -> b.getDateRange().overlaps(requestedRange))
                .count();

        return Math.max(0, roomType.getTotalInventory() - (int) overlappingActiveBookings);
    }
}
