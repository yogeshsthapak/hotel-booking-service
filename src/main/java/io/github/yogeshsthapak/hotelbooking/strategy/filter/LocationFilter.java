package io.github.yogeshsthapak.hotelbooking.strategy.filter;

import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import org.springframework.stereotype.Component;

@Component
public class LocationFilter implements PropertyFilter {
    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria.city() != null && !criteria.city().isBlank()) {
            if (!property.getLocation().city().equalsIgnoreCase(criteria.city().trim())) {
                return false;
            }
        }
        if (criteria.locality() != null && !criteria.locality().isBlank()) {
            if (!property.getLocation().locality().equalsIgnoreCase(criteria.locality().trim())) {
                return false;
            }
        }
        return true;
    }
}
