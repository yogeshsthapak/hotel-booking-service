package io.github.yogeshsthapak.hotelbooking.repository.impl;

import io.github.yogeshsthapak.hotelbooking.domain.model.Owner;
import io.github.yogeshsthapak.hotelbooking.repository.OwnerRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOwnerRepository implements OwnerRepository {
    private final Map<String, Owner> store = new ConcurrentHashMap<>();

    @Override
    public Owner save(Owner owner) {
        store.put(owner.getId(), owner);
        return owner;
    }

    @Override
    public Optional<Owner> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}
