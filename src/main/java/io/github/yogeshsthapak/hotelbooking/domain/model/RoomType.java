package io.github.yogeshsthapak.hotelbooking.domain.model;

import io.github.yogeshsthapak.hotelbooking.domain.exception.DomainException;
import java.math.BigDecimal;
import java.util.UUID;

public class RoomType {
    private final String id;
    private final String name;
    private final int maxGuests;
    private final int totalInventory;
    private final BigDecimal basePricePerNight;

    public RoomType(String id, String name, int maxGuests, int totalInventory, BigDecimal basePricePerNight) {
        if (maxGuests <= 0) throw new DomainException("Max guests must be positive");
        if (totalInventory <= 0) throw new DomainException("Total inventory must be positive");
        if (basePricePerNight == null || basePricePerNight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Base price per night must be positive");
        }
        this.id = (id == null || id.isBlank()) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.maxGuests = maxGuests;
        this.totalInventory = totalInventory;
        this.basePricePerNight = basePricePerNight;
    }

    public boolean canAccommodate(int guests) {
        return guests > 0 && guests <= maxGuests;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getMaxGuests() { return maxGuests; }
    public int getTotalInventory() { return totalInventory; }
    public BigDecimal getBasePricePerNight() { return basePricePerNight; }
}
