package io.github.yogeshsthapak.hotelbooking.service;

import io.github.yogeshsthapak.hotelbooking.domain.enums.Amenity;
import io.github.yogeshsthapak.hotelbooking.domain.exception.ResourceNotFoundException;
import io.github.yogeshsthapak.hotelbooking.domain.model.Owner;
import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import io.github.yogeshsthapak.hotelbooking.domain.model.RoomType;
import io.github.yogeshsthapak.hotelbooking.domain.vo.Location;
import io.github.yogeshsthapak.hotelbooking.repository.OwnerRepository;
import io.github.yogeshsthapak.hotelbooking.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PropertyService {
    private final OwnerRepository ownerRepository;
    private final PropertyRepository propertyRepository;

    public PropertyService(OwnerRepository ownerRepository, PropertyRepository propertyRepository) {
        this.ownerRepository = ownerRepository;
        this.propertyRepository = propertyRepository;
    }

    public Owner registerOwner(String name, String email) {
        Owner owner = new Owner(null, name, email);
        return ownerRepository.save(owner);
    }

    public Owner getOwner(String ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerId));
    }

    public Property addProperty(String ownerId, String name, Location location,
                                int starRating, Set<Amenity> amenities, List<RoomType> roomTypes) {
        Owner owner = getOwner(ownerId);
        Property property = new Property(null, owner.getId(), name, location, starRating, amenities, roomTypes);
        propertyRepository.save(property);

        owner.addPropertyId(property.getId());
        ownerRepository.save(owner);
        return property;
    }

    public Property getProperty(String propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));
    }

    public List<Property> getPropertiesByOwner(String ownerId) {
        getOwner(ownerId);
        return propertyRepository.findByOwnerId(ownerId);
    }
}
