package io.github.yogeshsthapak.hotelbooking.repository;

import io.github.yogeshsthapak.hotelbooking.domain.model.Owner;
import java.util.Optional;

public interface OwnerRepository {
    Owner save(Owner owner);
    Optional<Owner> findById(String id);
}
