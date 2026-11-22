package io.github.yogeshsthapak.hotelbooking.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Owner {
    private final String id;
    private final String name;
    private final String email;
    private final List<String> propertyIds = new ArrayList<>();

    public Owner(String id, String name, String email) {
        this.id = (id == null || id.isBlank()) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.email = email;
    }

    public void addPropertyId(String propertyId) {
        if (!propertyIds.contains(propertyId)) {
            propertyIds.add(propertyId);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getPropertyIds() { return Collections.unmodifiableList(propertyIds); }
}
