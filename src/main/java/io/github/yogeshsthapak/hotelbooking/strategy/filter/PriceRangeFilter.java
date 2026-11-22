package io.github.yogeshsthapak.hotelbooking.strategy.filter;

import io.github.yogeshsthapak.hotelbooking.domain.model.Property;
import org.springframework.stereotype.Component;

@Component
public class PriceRangeFilter implements PropertyFilter {
    @Override
    public boolean matches(Property property, SearchCriteria criteria) {
        if (criteria.minPrice() == null && criteria.maxPrice() == null) {
            return true;
        }
        return property.getRoomTypes().stream().anyMatch(rt -> {
            boolean aboveMin = criteria.minPrice() == null
                    || rt.getBasePricePerNight().compareTo(criteria.minPrice()) >= 0;
            boolean belowMax = criteria.maxPrice() == null
                    || rt.getBasePricePerNight().compareTo(criteria.maxPrice()) <= 0;
            return aboveMin && belowMax;
        });
    }
}
