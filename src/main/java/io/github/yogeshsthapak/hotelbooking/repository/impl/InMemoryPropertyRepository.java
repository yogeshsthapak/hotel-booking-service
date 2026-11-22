package io.github.yogeshsthapak.hotelbooking.repository.impl;

import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import io.github.yogeshsthapak.hotelbooking.repository.PropertyRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPropertyRepository implements PropertyRepository {
    private final Map<String, Property> store = new ConcurrentHashMap<>();

    @Override
    public Property save(Property property) {
        store.put(property.getId(), property);
        return property;
    }

    @Override
    public Optional<Property> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Property> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Property> findByOwnerId(String ownerId) {
        return store.values().stream()
                .filter(p -> p.getOwnerId().equals(ownerId))
                .toList();
    }
}
