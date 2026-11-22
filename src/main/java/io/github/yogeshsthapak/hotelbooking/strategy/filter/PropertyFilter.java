package io.github.yogeshsthapak.hotelbooking.strategy.filter;

import io.github.yogeshsthapak.hotelbooking.domain.model.Property;

public interface PropertyFilter {
    boolean matches(Property property, SearchCriteria criteria);
}
