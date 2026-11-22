package io.github.yogeshsthapak.hotelbooking.strategy.filter;

import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import org.springframework.stereotype.Component;

@Component
public class AmenityFilter implements PropertyFilter {
    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria.requiredAmenities() == null || criteria.requiredAmenities().isEmpty()) {
            return true;
        }
        return property.getAmenities().containsAll(criteria.requiredAmenities());
    }
}
