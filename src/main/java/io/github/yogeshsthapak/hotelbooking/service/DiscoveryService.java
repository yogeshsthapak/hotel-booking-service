package io.github.yogeshsthapak.hotelbooking.service;

import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;
import io.github.yogeshsthapak.hotelbooking.repository.PropertyRepository;
import io.github.yogeshsthapak.hotelbooking.strategy.filter.PropertyFilter;
import io.github.yogeshsthapak.hotelbooking.strategy.filter.SearchCriteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscoveryService {
    private final PropertyRepository propertyRepository;
    private final InventoryService inventoryService;
    private final List<PropertyFilter> filters;

    public DiscoveryService(PropertyRepository propertyRepository,
                            InventoryService inventoryService,
                            List<PropertyFilter> filters) {
        this.propertyRepository = propertyRepository;
        this.inventoryService = inventoryService;
        this.filters = filters;
    }

    public List<Property> search(SearchCriteria criteria) {
        return propertyRepository.findAll().stream()
                .filter(property -> matchesAllFilters(property, criteria))
                .filter(property -> hasAvailableMatchingRoom(property, criteria))
                .toList();
    }

    private boolean matchesAllFilters(Property property, SearchCriteria criteria) {
        return filters.stream().allMatch(filter -> filter.matches(property, criteria));
    }

    private boolean hasAvailableMatchingRoom(Property property, SearchCriteria criteria) {
        return property.getRoomTypes().stream()
                .anyMatch(roomType -> isRoomSuitableAndAvailable(property.getId(), roomType, criteria));
    }

    private boolean isRoomSuitableAndAvailable(String propertyId, RoomType roomType, SearchCriteria criteria) {
        if (criteria.guests() != null && !roomType.canAccommodate(criteria.guests())) {
            return false;
        }
        if (criteria.dateRange() != null) {
            return inventoryService.getAvailableRooms(propertyId, roomType, criteria.dateRange()) > 0;
        }
        return true;
    }
}
